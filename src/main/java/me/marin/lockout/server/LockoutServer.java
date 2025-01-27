package me.marin.lockout.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.marin.lockout.*;
import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.generator.BoardGenerator;
import me.marin.lockout.generator.GoalRequirements;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.goals.death.DieToFallingOffVinesGoal;
import me.marin.lockout.lockout.goals.death.DieToTNTMinecartGoal;
import me.marin.lockout.lockout.goals.have_more.HaveMostXPLevelsGoal;
import me.marin.lockout.lockout.goals.kill.Kill100MobsGoal;
import me.marin.lockout.lockout.goals.kill.KillColoredSheepGoal;
import me.marin.lockout.lockout.goals.kill.KillOtherTeamPlayer;
import me.marin.lockout.lockout.goals.kill.KillSnowGolemInNetherGoal;
import me.marin.lockout.lockout.goals.misc.EmptyHungerBarGoal;
import me.marin.lockout.lockout.goals.misc.ReachBedrockGoal;
import me.marin.lockout.lockout.goals.misc.ReachHeightLimitGoal;
import me.marin.lockout.lockout.goals.misc.ReachNetherRoofGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentDies3TimesGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentDiesGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentTouchesWaterGoal;
import me.marin.lockout.lockout.interfaces.*;
import me.marin.lockout.network.CustomBoardPayload;
import me.marin.lockout.network.StartLockoutPayload;
import me.marin.lockout.network.UpdateTooltipPayload;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.damage.FallLocation;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.scoreboard.ServerScoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.command.LocateCommand;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.StatType;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameMode;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.dimension.DimensionTypes;
import net.minecraft.world.gen.structure.Structure;

import java.util.*;

import static me.marin.lockout.LockoutInitializer.BOARD_SIZE;

public class LockoutServer {

    public static final int LOCATE_SEARCH = 1500;
    public static final Map<RegistryKey<Biome>, LocateData> BIOME_LOCATE_DATA = new HashMap<>();
    public static final Map<RegistryKey<Structure>, LocateData> STRUCTURE_LOCATE_DATA = new HashMap<>();
    public static final List<DyeColor> AVAILABLE_DYE_COLORS = new ArrayList<>();

    private static int lockoutStartTime = 60;

    public static Lockout lockout;
    public static MinecraftServer server;
    public static CompassItemHandler compassHandler;

    public static final Map<LockoutRunnable, Long> gameStartRunnables = new HashMap<>();

    private static LockoutBoard CUSTOM_BOARD = null;

    private static boolean isInitialized = false;

    public static void initializeServer() {
        lockout = null;
        compassHandler = null;
        gameStartRunnables.clear();

        // Ideally, rejoining a world gets detected here, and this data doesn't get wiped
        BIOME_LOCATE_DATA.clear();
        STRUCTURE_LOCATE_DATA.clear();
        AVAILABLE_DYE_COLORS.clear();

        if (isInitialized) return;
        isInitialized = true;

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (ChatManager.getChat(sender) == ChatManager.Type.TEAM) {
                String m = "[Team Chat] " + Formatting.RESET + "<" + sender.getName().getString() + "> " + message.getContent().getString();
                if (Lockout.isLockoutRunning(lockout)) {
                    LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(sender.getUuid());
                    team.sendMessage(team.getColor() + m);
                } else {
                    Team team = sender.getScoreboardTeam();
                    if (team == null) {
                        return true;
                    }
                    MinecraftServer server = sender.getServer();
                    PlayerManager pm = server.getPlayerManager();

                    team.getPlayerList().stream().filter(p -> pm.getPlayer(p) != null).map(pm::getPlayer).forEach(p ->{
                        p.sendMessage(Text.literal(team.getColor() + m));
                    });
                }
                return false;
            }
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;
            if (lockout.isSoloBlackout()) return;
            if (!lockout.isLockoutPlayer(newPlayer.getUuid())) return;

            int slot = LockoutServer.compassHandler.compassSlots.getOrDefault(newPlayer.getUuid(), 0);
            if (slot == 40) {
                newPlayer.getInventory().offHand.set(0, compassHandler.newCompass());
            }
            if (slot >= 0 && slot <= 35) {
                newPlayer.getInventory().setStack(slot, compassHandler.newCompass());
            }
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (!(goal instanceof EnterDimensionGoal enterDimensionGoal)) continue;
                if (goal.isCompleted()) continue;

                if (destination.getDimensionEntry().equals(enterDimensionGoal.getDimensionTypeKey())) {
                    lockout.completeGoal(goal, player);
                }
            }
        });



        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;

            ServerPlayerEntity player = handler.getPlayer();

            if (lockout.isLockoutPlayer(player.getUuid())) {
                LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(player.getUuid());
                for (Goal goal : lockout.getBoard().getGoals()) {
                    if (goal instanceof HasTooltipInfo hasTooltipInfo) {
                        ServerPlayNetworking.send(player, new UpdateTooltipPayload(goal.getId(), String.join("\n", hasTooltipInfo.getTooltip(team))));
                    }
                }
                player.changeGameMode(GameMode.SURVIVAL);
            } else {
                for (Goal goal : lockout.getBoard().getGoals()) {
                    if (goal instanceof HasTooltipInfo hasTooltipInfo) {
                        ServerPlayNetworking.send(player, new UpdateTooltipPayload(goal.getId(), String.join("\n", hasTooltipInfo.getSpectatorTooltip())));
                    }
                }
                player.changeGameMode(GameMode.SPECTATOR);
                player.sendMessage(Text.literal("You are spectating this match.").formatted(Formatting.GRAY, Formatting.ITALIC));
            }

            ServerPlayNetworking.send(player, lockout.getTeamsGoalsPacket());
            ServerPlayNetworking.send(player, lockout.getUpdateTimerPacket());
            if (lockout.hasStarted()) {
                ServerPlayNetworking.send(player, StartLockoutPayload.INSTANCE);
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;

            for (LockoutRunnable runnable : new HashSet<>(gameStartRunnables.keySet())) {
                if (gameStartRunnables.get(runnable) <= 0) {
                    runnable.run();
                    gameStartRunnables.remove(runnable);
                    return;
                }
                gameStartRunnables.merge(runnable, -1L, Long::sum);
            }

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;

                if (goal instanceof HaveMostXPLevelsGoal) {
                    for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                        lockout.levels.put(player.getUuid(), player.isDead() ? 0 : player.experienceLevel);
                    }
                    lockout.recalculateXPGoal(goal);
                }

                if (goal.isCompleted()) continue;

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    if (goal instanceof ObtainItemsGoal obtainItemsGoal) {
                        if (obtainItemsGoal.satisfiedBy(player.getInventory())) {
                            if (goal instanceof OpponentObtainsItemGoal opponentObtainsItemGoal) {
                                lockout.complete1v1Goal(goal, player, false, opponentObtainsItemGoal.getMessage(player));
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
                                allow = saddleable.isSaddled();
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
                        if (player.getY() >= 320 && player.getWorld().getRegistryKey() == ServerWorld.OVERWORLD) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof ReachNetherRoofGoal) {
                        if (player.getY() >= 128 && player.getWorld().getRegistryKey() == ServerWorld.NETHER) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof ReachBedrockGoal) {
                        if (player.getY() < 10 && Objects.equals(player.getWorld().getBlockState(player.getBlockPos().down()).getBlock(), Blocks.BEDROCK)) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof OpponentTouchesWaterGoal) {
                        if (Objects.equals(player.getWorld().getBlockState(player.getBlockPos()).getBlock(), Blocks.WATER)) {
                            lockout.complete1v1Goal(goal, player, false, player.getName().getString() + " touched water.");
                        }
                    }
                }
            }

            lockout.tick();
            if (lockout.getTicks() % 20 == 0) {
                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    ServerPlayNetworking.send(player, lockout.getUpdateTimerPacket());
                }
            }
        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;

            if (entity instanceof PlayerEntity player) {
                lockout.deaths.putIfAbsent(player.getUuid(), 0);
                lockout.deaths.merge(player.getUuid(), 1, Integer::sum);
            } else {
                if (entity.getPrimeAdversary() instanceof PlayerEntity player) {
                    if (lockout.isLockoutPlayer(player.getUuid())) {
                        LockoutTeam team = lockout.getPlayerTeam(player.getUuid());
                        lockout.mobsKilled.putIfAbsent(team, 0);
                        lockout.mobsKilled.merge(team, 1, Integer::sum);
                    }
                }
            }

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (goal.isCompleted()) continue;

                if (entity.getPrimeAdversary() instanceof PlayerEntity attackerPlayer) {
                    if (goal instanceof KillMobGoal killMobGoal) {
                        if (killMobGoal.getEntity().equals(entity.getType())) {
                            boolean allow = true;
                            if (killMobGoal instanceof KillSnowGolemInNetherGoal)  {
                                allow = (attackerPlayer.getWorld().getDimensionEntry().equals(DimensionTypes.THE_NETHER));
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
                    if (lockout.isLockoutPlayer(attackerPlayer.getUuid())) {
                        LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(attackerPlayer.getUuid());

                        if (goal instanceof KillAllSpecificMobsGoal killAllSpecificMobsGoal) {
                            if (killAllSpecificMobsGoal.getEntityTypes().contains(entity.getType())) {
                                killAllSpecificMobsGoal.getTrackerMap().computeIfAbsent(team, t -> new LinkedHashSet<>());
                                killAllSpecificMobsGoal.getTrackerMap().get(team).add(entity.getType());

                                int size = killAllSpecificMobsGoal.getTrackerMap().get(team).size();

                                team.sendTooltipUpdate((Goal & HasTooltipInfo) goal);
                                if (size >= killAllSpecificMobsGoal.getEntityTypes().size()) {
                                    lockout.completeGoal(killAllSpecificMobsGoal, team);
                                }
                            }
                        }
                        if (goal instanceof KillUniqueHostileMobsGoal killUniqueHostileMobsGoal) {
                            if (entity instanceof Monster) {
                                lockout.killedHostileTypes.computeIfAbsent(team, t -> new LinkedHashSet<>());
                                lockout.killedHostileTypes.get(team).add(entity.getType());

                                int size = lockout.killedHostileTypes.get(team).size();

                                team.sendTooltipUpdate((Goal & HasTooltipInfo) goal);
                                if (size >= killUniqueHostileMobsGoal.getAmount()) {
                                    lockout.completeGoal(killUniqueHostileMobsGoal, team);
                                }
                            }
                        }
                        if (goal instanceof Kill100MobsGoal kill100MobsGoal) {
                            int size = lockout.mobsKilled.get(team);

                            team.sendTooltipUpdate((Goal & HasTooltipInfo) goal);
                            if (size >= kill100MobsGoal.getAmount()) {
                                lockout.completeGoal(goal, team);
                            }
                        }
                        if (goal instanceof KillSpecificMobsGoal killSpecificMobsGoal) {
                            if (killSpecificMobsGoal.getEntityTypes().contains(entity.getType())) {
                                killSpecificMobsGoal.getTrackerMap().computeIfAbsent(team, t -> 0);
                                killSpecificMobsGoal.getTrackerMap().merge(team, 1, Integer::sum);

                                int size = killSpecificMobsGoal.getTrackerMap().get(team);

                                team.sendTooltipUpdate((Goal & HasTooltipInfo) goal);
                                if (size >= killSpecificMobsGoal.getAmount()) {
                                    lockout.completeGoal(killSpecificMobsGoal, attackerPlayer);
                                }
                            }
                        }
                    }

                }
                if (entity instanceof PlayerEntity player) {
                    if (goal instanceof OpponentDiesGoal) {
                        lockout.complete1v1Goal(goal, player, false, player.getName().getString() + " died.");
                    }
                    if (goal instanceof OpponentDies3TimesGoal && lockout.deaths.get(player.getUuid()) >= 3) {
                        lockout.complete1v1Goal(goal, player, false, player.getName().getString() + " died 3 times.");
                    }
                    if (goal instanceof DieToDamageTypeGoal dieToDamageTypeGoal) {
                        for (RegistryKey<DamageType> key : dieToDamageTypeGoal.getDamageRegistryKeys()) {
                            if (source.getTypeRegistryEntry().matchesKey(key)) {
                                lockout.completeGoal(goal, player);
                            }
                        }
                    }
                    if (goal instanceof DieToEntityGoal dieToEntityGoal) {
                        if (source.getAttacker() != null && source.getAttacker().getType() == dieToEntityGoal.getEntityType()) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                    if (goal instanceof DieToFallingOffVinesGoal) {
                        if (source.getTypeRegistryEntry().matchesKey(DamageTypes.FALL)) {
                            FallLocation fallLocation = FallLocation.fromEntity(player);
                            if (fallLocation != null) {
                                if (List.of(FallLocation.VINES, FallLocation.TWISTING_VINES, FallLocation.WEEPING_VINES).contains(fallLocation)) {
                                    lockout.completeGoal(goal, player);
                                }
                            }
                        }
                    }
                    if (goal instanceof DieToTNTMinecartGoal) {
                        if (source.getSource() instanceof TntMinecartEntity) {
                            lockout.completeGoal(goal, player);
                        }
                    }
                }

                if (goal instanceof KillOtherTeamPlayer) {
                    if (entity instanceof PlayerEntity player) {
                        if (entity.getPrimeAdversary() instanceof PlayerEntity killer) {
                            if (!Objects.equals(player, killer) && !Objects.equals(lockout.getPlayerTeam(killer.getUuid()), lockout.getPlayerTeam(player.getUuid()))) {
                                lockout.completeGoal(goal, killer);
                            }
                        }
                    }
                }
            }

        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            server.execute(() -> {
                Lockout.log("Locating all required Structures and Biomes");
                LockoutServer.server = server;
                long start = System.currentTimeMillis();

                AVAILABLE_DYE_COLORS.add(DyeColor.BLACK);
                AVAILABLE_DYE_COLORS.add(DyeColor.WHITE);
                AVAILABLE_DYE_COLORS.add(DyeColor.GRAY);
                AVAILABLE_DYE_COLORS.add(DyeColor.LIGHT_GRAY);
                AVAILABLE_DYE_COLORS.add(DyeColor.BLUE);
                AVAILABLE_DYE_COLORS.add(DyeColor.LIGHT_BLUE);
                AVAILABLE_DYE_COLORS.add(DyeColor.ORANGE);
                AVAILABLE_DYE_COLORS.add(DyeColor.RED);
                AVAILABLE_DYE_COLORS.add(DyeColor.YELLOW);
                AVAILABLE_DYE_COLORS.add(DyeColor.MAGENTA);
                AVAILABLE_DYE_COLORS.add(DyeColor.PINK);
                AVAILABLE_DYE_COLORS.add(DyeColor.PURPLE);

                boolean hasCactus = locateBiome(server, BiomeKeys.DESERT).isInRequiredDistance();
                hasCactus |= locateBiome(server, BiomeKeys.BADLANDS).isInRequiredDistance();
                hasCactus |= locateBiome(server, BiomeKeys.ERODED_BADLANDS).isInRequiredDistance();
                hasCactus |= locateBiome(server, BiomeKeys.WOODED_BADLANDS).isInRequiredDistance();
                if (hasCactus) {
                    AVAILABLE_DYE_COLORS.add(DyeColor.GREEN);
                    AVAILABLE_DYE_COLORS.add(DyeColor.LIME);
                    AVAILABLE_DYE_COLORS.add(DyeColor.CYAN);
                } else {
                    if (locateBiome(server, BiomeKeys.WARM_OCEAN).isInRequiredDistance()) {
                        AVAILABLE_DYE_COLORS.add(DyeColor.LIME);
                    }
                }

                boolean hasCocoaBeans;
                hasCocoaBeans  = locateBiome(server, BiomeKeys.JUNGLE).isInRequiredDistance();
                hasCocoaBeans |= locateBiome(server, BiomeKeys.BAMBOO_JUNGLE).isInRequiredDistance();
                hasCocoaBeans |= locateBiome(server, BiomeKeys.JUNGLE).isInRequiredDistance();
                if (hasCocoaBeans) {
                    AVAILABLE_DYE_COLORS.add(DyeColor.BROWN);
                }

                for (String id : GoalRegistry.INSTANCE.getRegisteredGoals()) {
                    GoalRequirements goalRequirements = GoalRegistry.INSTANCE.getGoalGenerator(id);
                    if (goalRequirements == null) continue;

                    for (RegistryKey<Biome> biome : goalRequirements.getRequiredBiomes()) {
                        locateBiome(server, biome);
                        // if (data.isInRequiredDistance()) break; // only one needs to be found, and this is a time-expensive operation
                    }

                    for (RegistryKey<Structure> structure : goalRequirements.getRequiredStructures()) {
                        locateStructure(server, structure);
                        // if (data.isInRequiredDistance()) break; // only one needs to be found, and this is a time-expensive operation
                    }
                }
                long end = System.currentTimeMillis();
                Lockout.log("Located " + BIOME_LOCATE_DATA.size() + " biomes and " + STRUCTURE_LOCATE_DATA.size() + " structures in " + String.format("%.2f", ((end-start)/1000.0)) + "s!");
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(CustomBoardPayload.ID, (payload, context) -> {
            ServerPlayerEntity player = context.player();

            if (!server.isSingleplayer()) {
                if (!player.hasPermissionLevel(2)) {
                    player.sendMessage(Text.literal("You do not have the permission for this command!").formatted(Formatting.RED));
                    return;
                }
            }

            boolean clearBoard = payload.boardOrClear().isEmpty();
            if (clearBoard) {
                CUSTOM_BOARD = null;
                player.sendMessage(Text.literal("Removed custom board."));
            } else {
                CUSTOM_BOARD = new LockoutBoard(payload.boardOrClear().get());
                player.sendMessage(Text.literal("Set custom board."));
            }
        });
    }

    public static LocateData locateBiome(MinecraftServer server, RegistryKey<Biome> biome) {
        if (BIOME_LOCATE_DATA.containsKey(biome)) return BIOME_LOCATE_DATA.get(biome);

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
        } else {
            int distance = MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), pair.getFirst().getX(), pair.getFirst().getZ()));
            data = new LocateData(true, distance < LOCATE_SEARCH, distance);
        }
        BIOME_LOCATE_DATA.put(biome, data);

        return data;
    }

    public static LocateData locateStructure(MinecraftServer server, RegistryKey<Structure> structure) {
        if (STRUCTURE_LOCATE_DATA.containsKey(structure)) return STRUCTURE_LOCATE_DATA.get(structure);

        var source = server.getCommandSource();
        var currentPos = BlockPos.ofFloored(source.getPosition());

        Registry<Structure> registry = source.getWorld().getRegistryManager().getOrThrow(RegistryKeys.STRUCTURE);
        RegistryEntryList<Structure> structureList = RegistryEntryList.of(registry.getOrThrow(structure));

        var pair = source.getWorld().getChunkManager().getChunkGenerator().locateStructure(
                source.getWorld(),
                structureList,
                currentPos,
                100,
                false);

        LocateData data;
        if (pair == null) {
            data = new LocateData(false, false,0);
        } else {
            int distance = MathHelper.floor(LocateCommand.getDistance(currentPos.getX(), currentPos.getZ(), pair.getFirst().getX(), pair.getFirst().getZ()));
            data = new LocateData(true, distance < LOCATE_SEARCH, distance);
        }
        STRUCTURE_LOCATE_DATA.put(structure, data);

        return data;
    }

    public static int lockoutCommandLogic(CommandContext<ServerCommandSource> context) {
        List<LockoutTeamServer> teams = new ArrayList<>();

        int ret = parseArgumentsIntoTeams(teams, context, false);
        if (ret == 0) return 0;

        startLockout(teams);

        return 1;
    }

    public static int blackoutCommandLogic(CommandContext<ServerCommandSource> context) {
        List<LockoutTeamServer> teams = new ArrayList<>();

        int ret = parseArgumentsIntoTeams(teams, context, true);
        if (ret == 0) return 0;

        startLockout(teams);

        return 1;
    }

    private static void startLockout(List<LockoutTeamServer> teams) {
        // Clear old runnables
        gameStartRunnables.clear();

        PlayerManager playerManager = server.getPlayerManager();
        List<ServerPlayerEntity> allServerPlayers = playerManager.getPlayerList();
        List<UUID> allLockoutPlayers = teams.stream()
                .flatMap(team -> team.getPlayers().stream())
                .toList();
        List<UUID> allSpectatorPlayers = allServerPlayers.stream()
                .map(ServerPlayerEntity::getUuid)
                .filter(uuid -> !allLockoutPlayers.contains(uuid))
                .toList();

        for (ServerPlayerEntity serverPlayer : allServerPlayers) {
            serverPlayer.getInventory().clear();
            serverPlayer.setHealth(serverPlayer.getMaxHealth());
            serverPlayer.clearStatusEffects();
            serverPlayer.getHungerManager().setSaturationLevel(5);
            serverPlayer.getHungerManager().setFoodLevel(20);
            serverPlayer.getHungerManager().exhaustion = 0.0f;
            serverPlayer.setExperienceLevel(0);
            serverPlayer.setExperiencePoints(0);
            serverPlayer.setOnFire(false);

            // Clear all stats
            for (@SuppressWarnings("unchecked") StatType<Object> statType : new StatType[]{Stats.CRAFTED, Stats.MINED, Stats.USED, Stats.BROKEN, Stats.PICKED_UP, Stats.DROPPED, Stats.KILLED, Stats.KILLED_BY, Stats.CUSTOM}) {
                for (Identifier id : statType.getRegistry().getIds()) {
                    serverPlayer.resetStat(statType.getOrCreateStat(statType.getRegistry().get(id)));
                }
            }
            serverPlayer.getStatHandler().sendStats(serverPlayer);
            // Clear all advancements
            AdvancementCommand.Operation.REVOKE.processAll(serverPlayer, server.getAdvancementLoader().getAdvancements());

            if (allLockoutPlayers.contains(serverPlayer.getUuid())) {
                serverPlayer.changeGameMode(GameMode.ADVENTURE);
            } else {
                serverPlayer.changeGameMode(GameMode.SPECTATOR);
                serverPlayer.sendMessage(Text.literal("You are spectating this match.").formatted(Formatting.GRAY, Formatting.ITALIC));
            }
        }

        ServerWorld world = server.getCommandSource().getWorld();
        int lockoutBoardSize = world.getGameRules().getInt(BOARD_SIZE);

        // Generate & set board
        LockoutBoard lockoutBoard;
        if (CUSTOM_BOARD == null) {
            BoardGenerator boardGenerator = new BoardGenerator(GoalRegistry.INSTANCE.getRegisteredGoals(), teams, AVAILABLE_DYE_COLORS, BIOME_LOCATE_DATA, STRUCTURE_LOCATE_DATA);
            lockoutBoard = boardGenerator.generateBoard(lockoutBoardSize);
        } else {
            // Reset custom board (TODO: do this somewhere else)
            for (Goal goal : CUSTOM_BOARD.getGoals()) {
                goal.setCompleted(false, null);
            }
            lockoutBoard = CUSTOM_BOARD;
        }

        lockout = new Lockout(lockoutBoard, teams);
        lockout.setTicks(-20L * lockoutStartTime); // see Lockout#ticks

        compassHandler = new CompassItemHandler(allLockoutPlayers, playerManager);

        List<Goal> tooltipGoals = new ArrayList<>(lockout.getBoard().getGoals()).stream().filter(g -> g instanceof HasTooltipInfo).toList();
        for (Goal goal : tooltipGoals) {
            // Update teams tooltip
            for (LockoutTeam team : lockout.getTeams()) {
                ((LockoutTeamServer) team).sendTooltipUpdate((Goal & HasTooltipInfo) goal, false);
            }
            // Update spectator tooltip
            if (!allSpectatorPlayers.isEmpty()) {
                var payload = new UpdateTooltipPayload(goal.getId(), String.join("\n", ((HasTooltipInfo) goal).getSpectatorTooltip()));
                for (UUID spectator : allSpectatorPlayers) {
                    ServerPlayNetworking.send(playerManager.getPlayer(spectator), payload);
                }
            }
        }

        for (ServerPlayerEntity player : allServerPlayers) {
            ServerPlayNetworking.send(player, lockout.getTeamsGoalsPacket());
            ServerPlayNetworking.send(player, lockout.getUpdateTimerPacket());

            if (!lockout.isSoloBlackout() && allLockoutPlayers.contains(player.getUuid())) {
                player.giveItemStack(compassHandler.newCompass());
            }
        }

        world.setTimeOfDay(0);

        for (int i = 3; i >= 0; i--) {
            if (i > 0) {
                final int secs = i;
                ((LockoutRunnable) () -> {
                    playerManager.broadcast(Text.literal("Starting in " + secs + "..."), false);
                }).runTaskAfter(20L * (lockoutStartTime - i));
            } else {
                ((LockoutRunnable) () -> {
                    lockout.setStarted(true);

                    for (ServerPlayerEntity player : allServerPlayers) {
                        if (player == null) continue;
                        ServerPlayNetworking.send(player, StartLockoutPayload.INSTANCE);
                        if (allLockoutPlayers.contains(player.getUuid())) {
                            player.changeGameMode(GameMode.SURVIVAL);
                        }
                    }
                    server.getPlayerManager().broadcast(Text.literal(lockout.getModeName() + " has begun."), false);
                }).runTaskAfter(20L * lockoutStartTime);
            }
        }
    }

    private static int parseArgumentsIntoTeams(List<LockoutTeamServer> teams, CommandContext<ServerCommandSource> context, boolean isBlackout) {
        String argument = null;

        PlayerManager playerManager = server.getPlayerManager();

        try {
            argument = context.getArgument("player names", String.class);
            String[] players = argument.split("\s+");
            if (isBlackout) {
                if (players.length == 0) {
                    context.getSource().sendError(Text.literal("Not enough players listed."));
                    return 0;
                }

                List<String> playerNames = new ArrayList<>();
                for (String player : players) {
                    if (playerManager.getPlayer(player) == null) {
                        context.getSource().sendError(Text.literal("Player " + player + " is invalid."));
                        return 0;
                    }
                    playerNames.add(playerManager.getPlayer(player).getName().getString());
                }
                teams.add(new LockoutTeamServer(playerNames, Formatting.byColorIndex(Lockout.COLOR_ORDERS[0]), server));

            } else {
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
            }

        } catch (Exception ignored) {}

        if (argument == null) {
            try {
                ServerScoreboard scoreboard = server.getScoreboard();

                argument = context.getArgument(isBlackout ? "team name" : "team names", String.class);
                String[] teamNames = argument.split("\s+");
                if (isBlackout) {
                    if (teamNames.length == 0) {
                        context.getSource().sendError(Text.literal("Not enough teams listed."));
                        return 0;
                    }
                    if (teamNames.length > 1) {
                        context.getSource().sendError(Text.literal("Only one team can play Blackout."));
                        return 0;
                    }
                } else {
                    if (teamNames.length < 2) {
                        context.getSource().sendError(Text.literal("Not enough teams listed. Make sure you separate team names with spaces."));
                        return 0;
                    }
                    if (teamNames.length > 16) {
                        context.getSource().sendError(Text.literal("Too many teams listed."));
                        return 0;
                    }
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
                    if (team.getPlayerList().isEmpty()) {
                        context.getSource().sendError(Text.literal("Team " + team.getName() + " doesn't have any players."));
                        return 0;
                    }
                    Formatting teamColor = team.getColor();
                    if (teamColor.getColorValue() == null || teamHasColor(teams, teamColor)) {
                        // Select an available color.
                        boolean found = false;
                        for (int colorOrder : Lockout.COLOR_ORDERS) {
                            if (!teamHasColor(teams, Formatting.byColorIndex(colorOrder))) {
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
            } catch (Exception ignored) {}
        }

        if (argument == null) {
            context.getSource().sendError(Text.literal("Illegal argument."));
            return 0;
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

    public static int setChat(CommandContext<ServerCommandSource> context, ChatManager.Type type) {
        ServerPlayerEntity player = context.getSource().getPlayer();
        if (player == null) {
            context.getSource().sendError(Text.literal("This is a player-only command."));
            return 0;
        }

        ChatManager.Type curr = ChatManager.getChat(player);
        if (curr == type) {
            player.sendMessage(Text.of("You are already chatting in " + type.name() + "."));
        } else {
            player.sendMessage(Text.of("You are now chatting in " + type.name() + "."));
            ChatManager.setChat(player, type);
        }
        return 1;
    }

    public static int giveGoal(CommandContext<ServerCommandSource> context) {
        try {
            if (!Lockout.isLockoutRunning(lockout)) {
                context.getSource().sendError(Text.literal("There's no active lockout match."));
                return 0;
            }

            int idx = context.getArgument("goal number", Integer.class);

            Collection<GameProfile> gps;
            try {
                gps = GameProfileArgumentType.getProfileArgument(context, "player name");
            } catch (CommandSyntaxException e) {
                context.getSource().sendError(Text.literal("Invalid target."));
                return 0;
            }

            if (gps.size() != 1) {
                context.getSource().sendError(Text.literal("Invalid number of targets."));
                return 0;
            }
            GameProfile gp = gps.stream().findFirst().get();
            if (!lockout.isLockoutPlayer(gp.getId())) {
                context.getSource().sendError(Text.literal("Player " + gp.getName() + " is not playing Lockout."));
                return 0;
            }

            if (idx > lockout.getBoard().getGoals().size()) {
                context.getSource().sendError(Text.literal("Goal number does not exist on the board."));
                return 0;
            }
            Goal goal = lockout.getBoard().getGoals().get(idx - 1);

            context.getSource().sendMessage(Text.of("Gave " + gp.getName() + " goal \"" + goal.getGoalName() + "\"."));
            lockout.updateGoalCompletion(goal, gp.getId());
            return 1;
        } catch (RuntimeException e) {
            Lockout.error(e);
            return 0;
        }
    }

    public static int setStartTime(CommandContext<ServerCommandSource> context) {
        int seconds = context.getArgument("seconds", Integer.class);

        lockoutStartTime = seconds;
        context.getSource().sendMessage(Text.of("Updated start time to " + seconds + "s."));
        return 1;
    }

}