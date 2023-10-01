package me.marin.lockout.server;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import me.marin.lockout.*;
import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalType;
import me.marin.lockout.lockout.goals.death.DieByFallingOffVinesGoal;
import me.marin.lockout.lockout.goals.death.DieByIronGolemGoal;
import me.marin.lockout.lockout.goals.death.DieByTNTMinecartGoal;
import me.marin.lockout.lockout.goals.kill.KillColoredSheepGoal;
import me.marin.lockout.lockout.goals.kill.KillSnowGolemInNetherGoal;
import me.marin.lockout.lockout.goals.misc.EmptyHungerBarGoal;
import me.marin.lockout.lockout.goals.misc.ReachHeightLimitGoal;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.*;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.damage.FallLocation;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionTypes;
import oshi.util.tuples.Pair;

import java.util.*;

public class LockoutServer implements DedicatedServerModInitializer {

    public static Map<LockoutRunnable, Integer> runnables = new HashMap<>();

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var commandNode = CommandManager.literal("lockout").build();
            var teamsNode = CommandManager.literal("teams").build();
            var playersNode = CommandManager.literal("players").build();
            var teamListNode = CommandManager.argument("team names", StringArgumentType.greedyString()).executes(LockoutServer::lockoutCommandLogic).build();
            var playerListNode = CommandManager.argument("player names", StringArgumentType.greedyString()).executes(LockoutServer::lockoutCommandLogic).build();

            dispatcher.getRoot().addChild(commandNode);
            commandNode.addChild(teamsNode);
            commandNode.addChild(playersNode);
            teamsNode.addChild(teamListNode);
            playersNode.addChild(playerListNode);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            if (!Lockout.isLockoutRunning()) return;

            Lockout lockout = Lockout.getInstance();

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (!(goal instanceof EnterDimensionGoal enterDimensionGoal)) continue;
                if (goal.isCompleted()) continue;

                if (destination.getDimensionKey().equals(enterDimensionGoal.getDimensionTypeKey())) {
                    lockout.completeGoal(goal, player);
                }
            }
        });

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!Lockout.isLockoutRunning()) return;

            ServerPlayNetworking.send(handler.getPlayer(), Constants.LOCKOUT_GOALS_TEAMS_PACKET, Lockout.getInstance().getTeamsGoalsPacket());
            if (Lockout.getInstance().hasStarted()) {
                ServerPlayNetworking.send(handler.getPlayer(), Constants.START_LOCKOUT_PACKET, Lockout.getInstance().getStartTimePacket());
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (!Lockout.isLockoutRunning()) return;

            for (LockoutRunnable runnable : new HashSet<>(runnables.keySet())) {
                if (runnables.get(runnable) == 0) {
                    runnable.run();
                    runnables.remove(runnable);
                }
                runnables.merge(runnable, -1, Integer::sum);
            }

            Lockout lockout = Lockout.getInstance();

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (goal.isCompleted()) continue;

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (goal instanceof ObtainItemsGoal obtainItemsGoal) {
                        if (obtainItemsGoal.satisfiedBy(player.getInventory())) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof RideEntityGoal rideEntityGoal && player.hasVehicle()) {
                        EntityType<?> vehicle = player.getVehicle().getType();

                        if (Objects.equals(vehicle, rideEntityGoal.getEntityType())) {
                            boolean allow = true;
                            if (player.getVehicle() instanceof Saddleable saddleable) {
                                allow &= saddleable.isSaddled();
                            }
                            if (Objects.equals(vehicle, EntityType.PIG)) {
                                boolean hasCarrotOnAStick = false;
                                for (ItemStack handItem : player.getHandItems()) {
                                    if (handItem.getItem().equals(Items.CARROT_ON_A_STICK)) {
                                        hasCarrotOnAStick = true;
                                        break;
                                    }
                                }
                                allow &= hasCarrotOnAStick;
                            }
                            if (allow) {
                                lockout.completeGoal(goal, player);
                            }
                        }
                    }
                    if (goal instanceof EmptyHungerBarGoal) {
                        if (player.getHungerManager().getFoodLevel() == 0) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof ReachHeightLimitGoal) {
                        if (player.getY() >= 320 && player.getWorld().getDimensionKey().equals(DimensionTypes.OVERWORLD)) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                }
            }

        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!Lockout.isLockoutRunning()) return;

            Lockout lockout = Lockout.getInstance();

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (goal.isCompleted()) continue;

                if (source.getAttacker() instanceof PlayerEntity attackerPlayer) {
                    if (goal instanceof KillMobGoal killMobGoal) {
                        if (killMobGoal.getEntity().equals(entity.getType())) {
                            boolean allow = true;
                            if (killMobGoal instanceof KillSnowGolemInNetherGoal)  {
                                allow = (attackerPlayer.getWorld().getDimensionKey().equals(DimensionTypes.THE_NETHER));
                            }
                            if (allow) {
                                lockout.completeGoal(goal, attackerPlayer);
                            }
                        }
                    }
                    if (goal instanceof KillColoredSheepGoal killColoredSheepGoal) {
                        if (EntityType.SHEEP.equals(entity.getType()) && ((SheepEntity) entity).getColor() == killColoredSheepGoal.getDyeColor()) {
                            lockout.completeGoal(goal, attackerPlayer);
                        }
                    }
                    if (goal instanceof KillUniqueHostileMobsGoal killUniqueHostileMobsGoal) {
                        if (entity instanceof Monster) {
                            lockout.killedHostileTypes.computeIfAbsent(attackerPlayer, player_ -> new HashSet<>());
                            lockout.killedHostileTypes.get(attackerPlayer).add(entity.getType());
                            int size = lockout.killedHostileTypes.get(attackerPlayer).size();

                            if (size >= killUniqueHostileMobsGoal.getAmount()) {
                                lockout.completeGoal(killUniqueHostileMobsGoal, attackerPlayer);
                            }
                        }
                    }
                    if (goal instanceof KillSpecificMobsGoal killSpecificMobsGoal) {
                        if (killSpecificMobsGoal.getEntityTypes().contains(entity.getType())) {
                            killSpecificMobsGoal.getTrackerMap().computeIfAbsent(attackerPlayer, player_ -> 0);
                            killSpecificMobsGoal.getTrackerMap().merge(attackerPlayer, 1, Integer::sum);

                            int size = killSpecificMobsGoal.getTrackerMap().get(attackerPlayer);
                            if (size >= killSpecificMobsGoal.getAmount()) {
                                lockout.completeGoal(killSpecificMobsGoal, attackerPlayer);
                            }
                        }
                    }
                }
                if (entity instanceof PlayerEntity player) {
                    if (goal instanceof DieToDamageTypeGoal dieToDamageTypeGoal) {
                        if (source.getTypeRegistryEntry().matchesKey(dieToDamageTypeGoal.getDamageRegistryKey())) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof DieByIronGolemGoal) {
                        if (source.getAttacker() instanceof IronGolemEntity) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof DieByFallingOffVinesGoal) {
                        if (source.getTypeRegistryEntry().matchesKey(DamageTypes.FALL)) {
                            FallLocation fallLocation = FallLocation.fromEntity(player);
                            if (fallLocation != null) {
                                if (List.of(FallLocation.VINES, FallLocation.TWISTING_VINES, FallLocation.WEEPING_VINES).contains(fallLocation)) {
                                    lockout.completeGoal(goal, player);
                                }
                            }
                        }
                    }
                    if (goal instanceof DieByTNTMinecartGoal) {
                        if (source.getSource() instanceof TntMinecartEntity) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                }
            }

        });

    }

    private static int lockoutCommandLogic(CommandContext<ServerCommandSource> context) {
        String argument = null;

        List<LockoutTeamServer> teams = new ArrayList<>();
        MinecraftServer server = context.getSource().getServer();
        PlayerManager playerManager = context.getSource().getServer().getPlayerManager();

        try {
            argument = context.getArgument("player names", String.class);
            String[] players = argument.split("\s+");
            if (players.length < 2) {
                context.getSource().sendError(Text.literal("Not enough players listed. Make sure you separate player names with spaces."));
                return 0;
            }
            if (players.length > 16) {
                context.getSource().sendError(Text.literal("Too many players listed."));
                return 0;
            }

            for (int i = 0; i < players.length; i++) {
                String player = players[i];
                if (playerManager.getPlayer(player) == null) {
                    context.getSource().sendError(Text.literal("Player " + player + " is invalid."));
                    return 0;
                }
                teams.add(new LockoutTeamServer(List.of(player), Formatting.byColorIndex(Lockout.COLOR_ORDERS[i]), server));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (argument == null) {
            try {
                ServerScoreboard scoreboard = server.getScoreboard();

                argument = context.getArgument("team names", String.class);
                String[] teamNames = argument.split("\s+");
                if (teamNames.length < 2) {
                    context.getSource().sendError(Text.literal("Not enough teams listed. Make sure you separate team names with spaces."));
                    return 0;
                }
                if (teamNames.length > 16) {
                    context.getSource().sendError(Text.literal("Too many teams listed."));
                    return 0;
                }
                List<Team> scoreboardTeams = new ArrayList<>();
                for (String teamName : teamNames) {
                    Team team = scoreboard.getTeam(teamName);
                    if (team == null) {
                        context.getSource().sendError(Text.literal("Team " + teamName + " is invalid."));
                        return 0;
                    }
                    for (String player : team.getPlayerList()) {
                        if (playerManager.getPlayer(player) == null) {
                            context.getSource().sendError(Text.literal("Player " + player + " on team " + teamName + " is invalid. Remove them from the team and try again."));
                            return 0;
                        }
                    }
                    scoreboardTeams.add(team);
                }
                for (Team team : scoreboardTeams) {
                    Formatting teamColor = team.getColor();
                    if (teamColor.getColorValue() == null || teamHasColor(teams, teamColor)) {
                        // Select an available color.
                        boolean found = false;
                        for (int colorOrder : Lockout.COLOR_ORDERS) {
                            if (!teamHasColor(teams, Formatting.byColorIndex(colorOrder))) {
                                System.out.println(Formatting.byColorIndex(colorOrder).asString());
                                found = true;
                                team.setColor(Formatting.byColorIndex(colorOrder));
                                break;
                            }
                        }
                        if (!found) {
                            context.getSource().sendError(Text.literal("Could not find assignable color for team " + team.getName() + ". Try recreating teams."));
                            return 0;
                        }
                    }
                    teams.add(new LockoutTeamServer(new ArrayList<>(team.getPlayerList()), team.getColor(), server));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (argument == null) {
            context.getSource().sendError(Text.literal("Illegal argument."));
            return 0;
        }

        List<Pair<String, String>> goals = List.of(
                new Pair<>(GoalType.USE_GRINDSTONE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.GET_20_ADVANCEMENTS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.GET_30_ADVANCEMENTS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.EAT_CAKE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.DIE_BY_TNT_MINECART, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.WEAR_CARVED_PUMPKIN_FOR_5_MINUTES, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.USE_COMPOSTER, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.USE_JUKEBOX, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.USE_GLOW_INK, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.EMPTY_HUNGER_BAR, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.REACH_HEIGHT_LIMIT, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.REACH_BEDROCK, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_6_UNIQUE_FLOWERS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_CLOCK, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_6_UNIQUE_BUCKETS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_ALL_MINECARTS, GoalDataConstants.DATA_COLOR_RED),
                new Pair<>(GoalType.OBTAIN_ALL_MUSHROOMS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_7_UNIQUE_WORKSTATIONS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_REDSTONE_LAMP, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_SOUL_CAMPFIRE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_ALL_PUMPKINS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.ENRAGE_ZOMBIFIED_PIGLIN, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_BRICK_WALL, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.WEAR_UNIQUE_COLORED_LEATHER_ARMOR, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.GET_10_ADVANCEMENTS, GoalDataConstants.DATA_NONE)
        );


        List<ServerPlayerEntity> allPlayers = new ArrayList<>();
        teams.forEach((team) -> {
            allPlayers.addAll(team.getPlayers());
        });

        for (ServerPlayerEntity serverPlayer : allPlayers) {
            serverPlayer.getInventory().clear();
            serverPlayer.changeGameMode(GameMode.SURVIVAL);
            serverPlayer.setHealth(serverPlayer.getMaxHealth());
            serverPlayer.clearStatusEffects();
            serverPlayer.getHungerManager().setExhaustion(0);
            serverPlayer.getHungerManager().setSaturationLevel(5);
            serverPlayer.getHungerManager().setFoodLevel(20);
            serverPlayer.setExperienceLevel(0);
            serverPlayer.setExperiencePoints(0);
        }
        // TODO: clear all stats

        // Clear all advancements
        ServerPlayerEntity serverPlayerEntity;
        for(var it = allPlayers.iterator(); it.hasNext(); AdvancementCommand.Operation.REVOKE.processAll(serverPlayerEntity, context.getSource().getServer().getAdvancementLoader().getAdvancements())) {
            serverPlayerEntity = it.next();
        }

        // Create lockout instance
        Lockout lockout = new Lockout(new LockoutBoard(goals), teams);
        PacketByteBuf buf = lockout.getTeamsGoalsPacket();

        for (ServerPlayerEntity player : allPlayers) {
            ServerPlayNetworking.send(player, Constants.LOCKOUT_GOALS_TEAMS_PACKET, buf);
        }

        ((LockoutRunnable) () -> {
            playerManager.broadcast(Text.literal("Starting in 3..."), false);
        }).runTaskAfter(20 * 7);
        ((LockoutRunnable) () -> {
            playerManager.broadcast(Text.literal("Starting in 2..."), false);
        }).runTaskAfter(20 * 8);
        ((LockoutRunnable) () -> {
            playerManager.broadcast(Text.literal("Starting in 1..."), false);
        }).runTaskAfter(20 * 9);
        ((LockoutRunnable) () -> {
            lockout.setStarted(true);
            lockout.setStartTime(System.currentTimeMillis());

            for (LockoutTeam team : teams) {
                if (team instanceof LockoutTeamServer lockoutTeamServer) {
                    for (ServerPlayerEntity player : lockoutTeamServer.getPlayers()) {
                        ServerPlayNetworking.send(player, Constants.START_LOCKOUT_PACKET, Lockout.getInstance().getStartTimePacket());
                    }
                }
            }
            context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lockout has begun."), false);
        }).runTaskAfter(20 * 10);

        return 1;
    }

    private static boolean teamHasColor(List<LockoutTeamServer> teams, Formatting color) {
        for (LockoutTeam lockoutTeam : teams) {
            if (lockoutTeam.getColor() == color) {
                return true;
            }
        }
        return false;
    }

}
