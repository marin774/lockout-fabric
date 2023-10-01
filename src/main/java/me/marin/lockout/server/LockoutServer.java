package me.marin.lockout.server;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.datafixers.util.Either;
import me.marin.lockout.*;
import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.GoalType;
import me.marin.lockout.lockout.goals.death.DieByFallingOffVinesGoal;
import me.marin.lockout.lockout.goals.death.DieByIronGolemGoal;
import me.marin.lockout.lockout.goals.death.DieByTNTMinecartGoal;
import me.marin.lockout.lockout.goals.kill.KillColoredSheepGoal;
import me.marin.lockout.lockout.goals.kill.KillOtherTeamPlayer;
import me.marin.lockout.lockout.goals.kill.KillSnowGolemInNetherGoal;
import me.marin.lockout.lockout.goals.misc.EmptyHungerBarGoal;
import me.marin.lockout.lockout.goals.misc.ReachBedrockGoal;
import me.marin.lockout.lockout.goals.misc.ReachHeightLimitGoal;
import me.marin.lockout.lockout.goals.misc.ReachNetherRoofGoal;
import me.marin.lockout.lockout.goals.more.HaveMoreXPLevelsGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentDies3TimesGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentDiesGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentTouchesWaterGoal;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.*;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
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
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.structure.Structure;
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Function;

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

                if (goal instanceof HaveMoreXPLevelsGoal) {
                    int mostLevels = 0;
                    PlayerEntity mostLevelsPlayer = null;

                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        if (player.isDead()) continue;
                        if (player.experienceLevel > mostLevels) {
                            mostLevelsPlayer = player;
                            mostLevels = player.experienceLevel;
                        }
                    }

                    if (mostLevelsPlayer != null) {
                        if (!Objects.equals(lockout.mostLevelsPlayer, mostLevelsPlayer.getUuid())) {
                            lockout.mostLevelsPlayer = mostLevelsPlayer.getUuid();
                            lockout.updateGoalCompletion(goal, mostLevelsPlayer);
                        }
                    } else {
                        if (lockout.mostLevelsPlayer != null) {
                            lockout.mostLevelsPlayer = null;
                            lockout.clearGoalCompletion(goal, server, true);
                        }
                    }
                }

                if (goal.isCompleted()) continue;

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (goal instanceof ObtainItemsGoal obtainItemsGoal) {
                        if (obtainItemsGoal.satisfiedBy(player.getInventory())) {
                            if (goal instanceof OpponentObtainsItemGoal opponentObtainsItemGoal) {
                                lockout.opponentCompletedGoal(goal, player, opponentObtainsItemGoal.getMessage(player));
                            } else {
                                lockout.completeGoal(goal, player);
                            }
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
                    if (goal instanceof ReachNetherRoofGoal) {
                        if (player.getY() >= 128 && player.getWorld().getDimensionKey().equals(DimensionTypes.THE_NETHER)) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof ReachBedrockGoal) {
                        if (Objects.equals(player.getWorld().getBlockState(player.getBlockPos().down()).getBlock(), Blocks.BEDROCK)) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof OpponentTouchesWaterGoal) {
                        if (Objects.equals(player.getWorld().getBlockState(player.getBlockPos()).getBlock(), Blocks.WATER)) {
                            lockout.opponentCompletedGoal(goal, player, player.getName().getString() + " touched water.");
                        }
                    }
                }
            }

        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!Lockout.isLockoutRunning()) return;

            Lockout lockout = Lockout.getInstance();

            if (entity instanceof PlayerEntity player) {
                lockout.deaths.putIfAbsent(player.getUuid(), 0);
                lockout.deaths.merge(player.getUuid(), 1, Integer::sum);
            }

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
                        //noinspection ConstantConditions
                        if (EntityType.SHEEP.equals(entity.getType()) && ((SheepEntity) entity).getColor() == killColoredSheepGoal.getDyeColor()) {
                            lockout.completeGoal(goal, attackerPlayer);
                        }
                    }
                    if (goal instanceof KillUniqueHostileMobsGoal killUniqueHostileMobsGoal) {
                        if (entity instanceof Monster) {
                            lockout.killedHostileTypes.computeIfAbsent(attackerPlayer.getUuid(), player_ -> new HashSet<>());
                            lockout.killedHostileTypes.get(attackerPlayer.getUuid()).add(entity.getType());
                            int size = lockout.killedHostileTypes.get(attackerPlayer.getUuid()).size();

                            if (size >= killUniqueHostileMobsGoal.getAmount()) {
                                lockout.completeGoal(killUniqueHostileMobsGoal, attackerPlayer);
                            }
                        }
                    }
                    if (goal instanceof KillSpecificMobsGoal killSpecificMobsGoal) {
                        if (killSpecificMobsGoal.getEntityTypes().contains(entity.getType())) {
                            killSpecificMobsGoal.getTrackerMap().computeIfAbsent(attackerPlayer.getUuid(), player_ -> 0);
                            killSpecificMobsGoal.getTrackerMap().merge(attackerPlayer.getUuid(), 1, Integer::sum);

                            int size = killSpecificMobsGoal.getTrackerMap().get(attackerPlayer.getUuid());
                            if (size >= killSpecificMobsGoal.getAmount()) {
                                lockout.completeGoal(killSpecificMobsGoal, attackerPlayer);
                            }
                        }
                    }
                }
                if (entity instanceof PlayerEntity player) {
                    if (goal instanceof OpponentDiesGoal) {
                        lockout.opponentCompletedGoal(goal, player, player.getName().getString() + " died.");
                    }
                    if (goal instanceof OpponentDies3TimesGoal && lockout.deaths.get(player.getUuid()) >= 3) {
                        lockout.opponentCompletedGoal(goal, player, player.getName().getString() + " died 3 times.");
                    }
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

                if (goal instanceof KillOtherTeamPlayer) {
                    if (entity instanceof PlayerEntity player) {
                        if (entity.getPrimeAdversary() instanceof PlayerEntity killer) {
                            if (!Objects.equals(player, killer)) {
                                if (!Objects.equals(lockout.getPlayerTeam(killer), lockout.getPlayerTeam((PlayerEntity) entity))) {
                                    lockout.completeGoal(goal, killer);
                                }
                            }
                        }
                    }
                }
            }

        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            Lockout.log("Locating all required Structures and Biomes");
            long start = System.currentTimeMillis();

            boolean hasCactus = false;
            hasCactus |= locateBiome(server, BiomeKeys.DESERT).isInRequiredDistance();
            hasCactus |= locateBiome(server, BiomeKeys.BADLANDS).isInRequiredDistance();
            hasCactus |= locateBiome(server, BiomeKeys.ERODED_BADLANDS).isInRequiredDistance();
            hasCactus |= locateBiome(server, BiomeKeys.WOODED_BADLANDS).isInRequiredDistance();

            availableDyeColors.add(DyeColor.BLACK);
            availableDyeColors.add(DyeColor.WHITE);
            availableDyeColors.add(DyeColor.GRAY);
            availableDyeColors.add(DyeColor.LIGHT_GRAY);
            availableDyeColors.add(DyeColor.BLUE);
            availableDyeColors.add(DyeColor.LIGHT_BLUE);
            availableDyeColors.add(DyeColor.ORANGE);
            availableDyeColors.add(DyeColor.RED);
            availableDyeColors.add(DyeColor.YELLOW);
            availableDyeColors.add(DyeColor.MAGENTA);
            availableDyeColors.add(DyeColor.PINK);
            availableDyeColors.add(DyeColor.PURPLE);

            if (hasCactus) {
                availableDyeColors.add(DyeColor.GREEN);
                availableDyeColors.add(DyeColor.LIME);
                availableDyeColors.add(DyeColor.CYAN);
            } else {
                if (locateBiome(server, BiomeKeys.WARM_OCEAN).isInRequiredDistance()) {
                    availableDyeColors.add(DyeColor.LIME);
                }
            }

            boolean hasCocoaBeans = false;
            hasCocoaBeans |= locateBiome(server, BiomeKeys.JUNGLE).isInRequiredDistance();
            hasCocoaBeans |= locateBiome(server, BiomeKeys.BAMBOO_JUNGLE).isInRequiredDistance();
            hasCocoaBeans |= locateBiome(server, BiomeKeys.JUNGLE).isInRequiredDistance();

            if (hasCocoaBeans) {
                availableDyeColors.add(DyeColor.BROWN);
            }

            Lockout.log("Available colors: " + String.join(", ", availableDyeColors.stream().map(GoalDataConstants::getDyeColorFormatted).toList()));

            for (String id : GoalRegistry.INSTANCE.getRegisteredGoals()) {
                GoalGeneratorProvider goalGeneratorProvider = GoalRegistry.INSTANCE.getGoalGenerator(id);
                if (goalGeneratorProvider == null) continue;

                if (goalGeneratorProvider.getRequiredBiomes() != null) {
                    for (RegistryKey<Biome> biome : goalGeneratorProvider.getRequiredBiomes()) {
                        locateBiome(server, biome);
                    }
                }

                if (goalGeneratorProvider.getRequiredStructures() != null) {
                    for (RegistryKey<Structure> structure : goalGeneratorProvider.getRequiredStructures()) {
                        locateStructure(server, structure);
                    }
                }
            }
            long end = System.currentTimeMillis();
            Lockout.log("Located " + BIOME_LOCATE_DATA.size() + " biomes and " + STRUCTURE_LOCATE_DATA.size() + " structures in " + String.format("%.2f", ((end-start)/1000.0)) + "s!");
        });
    }

    public static final int LOCATE_SEARCH = 1500;
    public static final Map<RegistryKey<Biome>, LocateData> BIOME_LOCATE_DATA = new HashMap<>();
    public static final Map<RegistryKey<Structure>, LocateData> STRUCTURE_LOCATE_DATA = new HashMap<>();
    public static final List<DyeColor> availableDyeColors = new ArrayList<>();

    public static LocateData locateBiome(MinecraftServer server, RegistryKey<Biome> biome) {
        if (BIOME_LOCATE_DATA.containsKey(biome)) return BIOME_LOCATE_DATA.get(biome);

        Lockout.log("Locating biome " + biome.getValue());

        var source = server.getCommandSource();
        var currentPos = BlockPos.ofFloored(source.getPosition());

        var pair = source.getWorld().locateBiome(
                biomeRegistryEntry -> biomeRegistryEntry.matchesId(biome.getValue()),
                currentPos,
                LOCATE_SEARCH,
                32,
                64);

        LocateData data;
        if (pair == null) {
            data = new LocateData(false, false,0);
            BIOME_LOCATE_DATA.put(biome, data);
        } else {
            int distance = MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), pair.getFirst().getX(), pair.getFirst().getZ()));
            data = new LocateData(true, distance < LOCATE_SEARCH, distance);
            BIOME_LOCATE_DATA.put(biome, data);
        }
        return data;
    }

    public static LocateData locateStructure(MinecraftServer server, RegistryKey<Structure> structure) {
        if (STRUCTURE_LOCATE_DATA.containsKey(structure)) return STRUCTURE_LOCATE_DATA.get(structure);

        Lockout.log("Locating structure " + structure.getValue());

        var source = server.getCommandSource();
        var currentPos = BlockPos.ofFloored(source.getPosition());

        Registry<Structure> registry = source.getWorld().getRegistryManager().get(RegistryKeys.STRUCTURE);
        Either<RegistryKey<Structure>, TagKey<Structure>> either = Either.left(structure);
        Function<RegistryKey<Structure>, Optional<RegistryEntryList<Structure>>> function = (key) -> registry.getEntry(key).map(RegistryEntryList::of);
        RegistryEntryList<Structure> structureList = either.map(function, registry::getEntryList).get();

        var pair = source.getWorld().getChunkManager().getChunkGenerator().locateStructure(
                source.getWorld(),
                structureList,
                currentPos,
                100,
                false);

        LocateData data;
        if (pair == null) {
            data = new LocateData(false, false,0);
            STRUCTURE_LOCATE_DATA.put(structure, data);
        } else {
            int distance = MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), pair.getFirst().getX(), pair.getFirst().getZ()));
            data = new LocateData(true, distance < LOCATE_SEARCH, distance);
            STRUCTURE_LOCATE_DATA.put(structure, data);
        }
        return data;
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
                teams.add(new LockoutTeamServer(List.of(playerManager.getPlayer(player).getName().getString()), Formatting.byColorIndex(Lockout.COLOR_ORDERS[i]), server));
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
                    if (team.getPlayerList().size() == 0) {
                        context.getSource().sendError(Text.literal("Team " + team.getName() + " doesn't have any players."));
                        return 0;
                    }
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
                    List<String> actualPlayerNames = new ArrayList<>();
                    for (String playerName : team.getPlayerList()) {
                        actualPlayerNames.add(playerManager.getPlayer(playerName).getName().getString());
                    }
                    teams.add(new LockoutTeamServer(new ArrayList<>(actualPlayerNames), team.getColor(), server));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        if (argument == null) {
            context.getSource().sendError(Text.literal("Illegal argument."));
            return 0;
        }

        runnables.clear();

        List<ServerPlayerEntity> allPlayers = new ArrayList<>();
        teams.forEach((team) -> {
            for (UUID uuid : team.getPlayers()) {
                allPlayers.add(playerManager.getPlayer(uuid));
            }
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
        Lockout lockout = new Lockout(new LockoutBoard(generateGoals()), teams);
        PacketByteBuf buf = lockout.getTeamsGoalsPacket();

        for (ServerPlayerEntity player : allPlayers) {
            ServerPlayNetworking.send(player, Constants.LOCKOUT_GOALS_TEAMS_PACKET, buf);
        }

        int timeToStart = 10;
        for (int i = 3; i >= 0; i--) {
            if (i > 0) {
                int secs = i;
                ((LockoutRunnable) () -> {
                    playerManager.broadcast(Text.literal("Starting in " + secs + "..."), false);
                }).runTaskAfter(20 * (timeToStart - i));
            } else {
                ((LockoutRunnable) () -> {
                    lockout.setStarted(true);
                    lockout.setStartTime(System.currentTimeMillis());

                    for (ServerPlayerEntity player : allPlayers) {
                        if (player == null) continue;
                        ServerPlayNetworking.send(player, Constants.START_LOCKOUT_PACKET, Lockout.getInstance().getStartTimePacket());
                    }
                    context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lockout has begun."), false);
                }).runTaskAfter(20 * timeToStart);
            }
        }
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

    private static List<Pair<String, String>> generateGoals() {
        List<Pair<String, String>> goals = List.of(
                new Pair<>(GoalType.KILL_OTHER_PLAYER, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.REACH_BEDROCK, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_CATCHES_ON_FIRE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_OBTAINS_SEEDS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_OBTAINS_OBSIDIAN, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_OBTAINS_CRAFTING_TABLE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_DIES, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_DIES_3_TIMES, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_HIT_BY_EGG, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_HIT_BY_SNOWBALL, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_JUMPS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_TAKES_100_DAMAGE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_TAKES_FALL_DAMAGE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OPPONENT_TOUCHES_WATER, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.TAKE_200_DAMAGE, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.HAVE_MORE_XP_LEVELS, GoalDataConstants.DATA_COLOR_RED),
                new Pair<>(GoalType.REACH_NETHER_ROOF, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_7_UNIQUE_WORKSTATIONS, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_REDSTONE_LAMP, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.FREEZE_TO_DEATH, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.GET_OH_SHINY_ADVANCEMENT, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.ENRAGE_ZOMBIFIED_PIGLIN, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_BRICK_WALL, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.WEAR_UNIQUE_COLORED_LEATHER_ARMOR, GoalDataConstants.DATA_NONE),
                new Pair<>(GoalType.OBTAIN_DISPENSER, GoalDataConstants.DATA_NONE)
        );

        /*
        List<Pair<String, String>> goals = new ArrayList<>();
        List<String> registeredGoals = new ArrayList<>(GoalRegistry.INSTANCE.getRegisteredGoals());
        Collections.shuffle(registeredGoals);
        ListIterator<String> iterator = registeredGoals.listIterator();
        while (goals.size() < 25 && iterator.hasNext()) {
            String goalId = iterator.next();
            Goal goal = GoalRegistry.INSTANCE.newGoal(goalId, GoalDataConstants.DATA_NONE);

        }

         */

        return goals;
    }

}
