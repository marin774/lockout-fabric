package me.marin.lockout.server;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import me.marin.lockout.*;
import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.generator.BoardGenerator;
import me.marin.lockout.generator.GoalRequirementsProvider;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.goals.death.DieByFallingOffVinesGoal;
import me.marin.lockout.lockout.goals.death.DieByIronGolemGoal;
import me.marin.lockout.lockout.goals.death.DieByTNTMinecartGoal;
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
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Saddleable;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.damage.FallLocation;
import net.minecraft.entity.mob.Monster;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
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
import oshi.util.tuples.Pair;

import java.util.*;
import java.util.function.Function;

public class LockoutServer {

    public static Lockout lockout;
    public static Map<LockoutRunnable, Integer> gameStartRunnables = new HashMap<>();
    public static MinecraftServer server;
    private static final int START_TIME = 60;
    public static Map<UUID, Integer> onDeathCompassSlot = new HashMap<>();
    public static final int LOCATE_SEARCH = 1500;
    public static final Map<RegistryKey<Biome>, LocateData> BIOME_LOCATE_DATA = new HashMap<>();
    public static final Map<RegistryKey<Structure>, LocateData> STRUCTURE_LOCATE_DATA = new HashMap<>();
    public static final List<DyeColor> availableDyeColors = new ArrayList<>();

    private static boolean isInitialized = false;

    public static void initializeServer() {
        lockout = null;
        gameStartRunnables.clear();
        onDeathCompassSlot.clear();

        // Ideally, rejoining a world gets detected here, and this data doesn't get wiped
        BIOME_LOCATE_DATA.clear();
        STRUCTURE_LOCATE_DATA.clear();
        availableDyeColors.clear();

        if (isInitialized) return;
        isInitialized = true;

        ServerMessageEvents.ALLOW_CHAT_MESSAGE.register((message, sender, params) -> {
            if (ChatManager.getChat(sender) == ChatManager.Type.TEAM) {
                String m = "[Team Chat] " + Formatting.RESET + "<" + sender.getName().getString() + "> " + message.getContent().getString();
                if (Lockout.isLockoutRunning(lockout)) {
                    LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(sender.getUuid());
                    team.sendMessage(team.getColor() + m);

                    return false;
                } else {
                    Team team = (Team) sender.getScoreboardTeam();
                    if (team == null) {
                        return true;
                    }
                    MinecraftServer server = sender.getServer();
                    PlayerManager pm = server.getPlayerManager();

                    team.getPlayerList().stream().filter(p -> pm.getPlayer(p) != null).map(pm::getPlayer).forEach(p ->{
                        p.sendMessage(Text.literal(team.getColor() + m));
                    });
                    return false;
                }
            }
            return true;
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!Lockout.exists(lockout)) return;
            if (lockout.isLockoutPlayer(newPlayer.getUuid())) {
                int slot = onDeathCompassSlot.getOrDefault(newPlayer.getUuid(), 0);
                if (slot == 40) {
                    newPlayer.getInventory().offHand.set(0, CompassItemHandler.newCompass());
                }
                if (slot >= 0 && slot <= 35) {
                    newPlayer.getInventory().setStack(slot, CompassItemHandler.newCompass());
                }
            }
        });

        LootTableEvents.REPLACE.register(((resourceManager, lootManager, id, original, source) -> {
            if (Objects.equals(id, LootTables.PIGLIN_BARTERING_GAMEPLAY)) {
                NbtCompound fireRes = new NbtCompound();
                fireRes.putString("Potion", "minecraft:fire_resistance");

                UniformLootNumberProvider ironNuggetsCount = UniformLootNumberProvider.create(9.0F, 36.0F);
                UniformLootNumberProvider quartzCount = UniformLootNumberProvider.create(8.0F, 16.0F);
                UniformLootNumberProvider glowstoneDustCount = UniformLootNumberProvider.create(5.0F, 12.0F);
                UniformLootNumberProvider magmaCreamCount = UniformLootNumberProvider.create(2.0F, 6.0F);
                UniformLootNumberProvider enderPearlCount = UniformLootNumberProvider.create(4.0F, 8.0F);
                UniformLootNumberProvider stringCount = UniformLootNumberProvider.create(8.0F, 24.0F);
                UniformLootNumberProvider fireChargeCount = UniformLootNumberProvider.create(1.0F, 5.0F);
                UniformLootNumberProvider gravelCount = UniformLootNumberProvider.create(8.0F, 16.0F);
                UniformLootNumberProvider leatherCount = UniformLootNumberProvider.create(4.0F, 10.0F);
                UniformLootNumberProvider netherBrickCount = UniformLootNumberProvider.create(4.0F, 16.0F);
                UniformLootNumberProvider cryingObsidianCount = UniformLootNumberProvider.create(1.0F, 3.0F);
                UniformLootNumberProvider soulSandCount = UniformLootNumberProvider.create(4.0F, 16.0F);

                @SuppressWarnings("deprecation")
                LootPool pool = LootPool.builder()
                        .with(ItemEntry.builder(Items.BOOK).apply(EnchantRandomlyLootFunction.create().add(Enchantments.SOUL_SPEED)).weight(5))
                        .with(ItemEntry.builder(Items.IRON_BOOTS).apply(EnchantRandomlyLootFunction.create().add(Enchantments.SOUL_SPEED)).weight(8))
                        .with(ItemEntry.builder(Items.POTION).apply(SetNbtLootFunction.builder(fireRes)).weight(10))
                        .with(ItemEntry.builder(Items.SPLASH_POTION).apply(SetNbtLootFunction.builder(fireRes)).weight(10))
                        .with(ItemEntry.builder(Items.IRON_NUGGET).apply(SetCountLootFunction.builder(ironNuggetsCount)).weight(10))
                        .with(ItemEntry.builder(Items.QUARTZ).apply(SetCountLootFunction.builder(quartzCount)).weight(20))
                        .with(ItemEntry.builder(Items.GLOWSTONE_DUST).apply(SetCountLootFunction.builder(glowstoneDustCount)).weight(20))
                        .with(ItemEntry.builder(Items.MAGMA_CREAM).apply(SetCountLootFunction.builder(magmaCreamCount)).weight(20))
                        .with(ItemEntry.builder(Items.ENDER_PEARL).apply(SetCountLootFunction.builder(enderPearlCount)).weight(20))
                        .with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(stringCount)).weight(20))
                        .with(ItemEntry.builder(Items.FIRE_CHARGE).apply(SetCountLootFunction.builder(fireChargeCount)).weight(40))
                        .with(ItemEntry.builder(Items.GRAVEL).apply(SetCountLootFunction.builder(gravelCount)).weight(40))
                        .with(ItemEntry.builder(Items.LEATHER).apply(SetCountLootFunction.builder(leatherCount)).weight(40))
                        .with(ItemEntry.builder(Items.NETHER_BRICK).apply(SetCountLootFunction.builder(netherBrickCount)).weight(40))
                        .with(ItemEntry.builder(Items.OBSIDIAN).weight(40))
                        .with(ItemEntry.builder(Items.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(cryingObsidianCount)).weight(40))
                        .with(ItemEntry.builder(Items.SOUL_SAND).apply(SetCountLootFunction.builder(soulSandCount)).weight(40))
                        .build();
                return LootTable.builder().pool(pool).build();
            }
            return null;
        }));

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;

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
            if (!Lockout.isLockoutRunning(lockout)) return;

            ServerPlayerEntity player = handler.getPlayer();

            if (lockout.isLockoutPlayer(player.getUuid())) {
                LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(player.getUuid());
                for (Goal goal : lockout.getBoard().getGoals()) {
                    if (goal instanceof HasTooltipInfo hasTooltipInfo) {
                        PacketByteBuf buf = PacketByteBufs.create();
                        buf.writeString(goal.getId());
                        buf.writeString(String.join("\n", hasTooltipInfo.getTooltip(team)));
                        ServerPlayNetworking.send(player, Constants.UPDATE_LORE, buf);
                    }
                }
            }

            ServerPlayNetworking.send(player, Constants.LOCKOUT_GOALS_TEAMS_PACKET, lockout.getTeamsGoalsPacket());
            if (lockout.hasStarted()) {
                ServerPlayNetworking.send(player, Constants.START_LOCKOUT_PACKET, lockout.getStartTimePacket());
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (!Lockout.isLockoutRunning(lockout)) return;

            for (LockoutRunnable runnable : new HashSet<>(gameStartRunnables.keySet())) {
                if (gameStartRunnables.get(runnable) == 0) {
                    runnable.run();
                    gameStartRunnables.remove(runnable);
                }
                gameStartRunnables.merge(runnable, -1, Integer::sum);
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
                    if (lockout.isLockoutPlayer(attackerPlayer.getUuid())) {
                        LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(attackerPlayer.getUuid());

                        if (goal instanceof KillAllSpecificMobsGoal killAllSpecificMobsGoal) {
                            if (killAllSpecificMobsGoal.getEntityTypes().contains(entity.getType())) {
                                killAllSpecificMobsGoal.getTrackerMap().computeIfAbsent(team, t -> new LinkedHashSet<>());
                                killAllSpecificMobsGoal.getTrackerMap().get(team).add(entity.getType());

                                int size = killAllSpecificMobsGoal.getTrackerMap().get(team).size();

                                team.sendLoreUpdate((Goal & HasTooltipInfo) goal);
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

                                team.sendLoreUpdate((Goal & HasTooltipInfo) goal);
                                if (size >= killUniqueHostileMobsGoal.getAmount()) {
                                    lockout.completeGoal(killUniqueHostileMobsGoal, team);
                                }
                            }
                        }
                        if (goal instanceof Kill100MobsGoal kill100MobsGoal) {
                            int size = lockout.mobsKilled.get(team);

                            team.sendLoreUpdate((Goal & HasTooltipInfo) goal);
                            if (size >= kill100MobsGoal.getAmount()) {
                                lockout.completeGoal(goal, team);
                            }
                        }
                        if (goal instanceof KillSpecificMobsGoal killSpecificMobsGoal) {
                            if (killSpecificMobsGoal.getEntityTypes().contains(entity.getType())) {
                                killSpecificMobsGoal.getTrackerMap().computeIfAbsent(team, t -> 0);
                                killSpecificMobsGoal.getTrackerMap().merge(team, 1, Integer::sum);

                                int size = killSpecificMobsGoal.getTrackerMap().get(team);

                                team.sendLoreUpdate((Goal & HasTooltipInfo) goal);
                                if (size >= killSpecificMobsGoal.getAmount()) {
                                    lockout.completeGoal(killSpecificMobsGoal, attackerPlayer);
                                }
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
                        for (RegistryKey<DamageType> key : dieToDamageTypeGoal.getDamageRegistryKeys()) {
                            if (source.getTypeRegistryEntry().matchesKey(key)) {
                                lockout.completeGoal(goal, player);
                            }
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
                            if (!Objects.equals(player, killer) && !Objects.equals(lockout.getPlayerTeam(killer.getUuid()), lockout.getPlayerTeam(player.getUuid()))) {
                                lockout.completeGoal(goal, killer);
                            }
                        }
                    }
                }
            }

        });

        ServerLifecycleEvents.SERVER_STARTED.register((server) -> {
            Lockout.log("Locating all required Structures and Biomes");
            LockoutServer.server = server;
            long start = System.currentTimeMillis();

            boolean hasCactus = locateBiome(server, BiomeKeys.DESERT).isInRequiredDistance();
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

            boolean hasCocoaBeans;
            hasCocoaBeans = locateBiome(server, BiomeKeys.JUNGLE).isInRequiredDistance();
            hasCocoaBeans |= locateBiome(server, BiomeKeys.BAMBOO_JUNGLE).isInRequiredDistance();
            hasCocoaBeans |= locateBiome(server, BiomeKeys.JUNGLE).isInRequiredDistance();

            if (hasCocoaBeans) {
                availableDyeColors.add(DyeColor.BROWN);
            }

            // Lockout.log("Available colors: " + String.join(", ", availableDyeColors.stream().map(GoalDataConstants::getDyeColorFormatted).toList()));

            for (String id : GoalRegistry.INSTANCE.getRegisteredGoals()) {
                GoalRequirementsProvider goalRequirementsProvider = GoalRegistry.INSTANCE.getGoalGenerator(id);
                if (goalRequirementsProvider == null) continue;

                if (goalRequirementsProvider.getRequiredBiomes() != null) {
                    for (RegistryKey<Biome> biome : goalRequirementsProvider.getRequiredBiomes()) {
                        locateBiome(server, biome);
                        // if (data.isInRequiredDistance()) break; // only one needs to be found, and this is a time-expensive operation
                    }
                }

                if (goalRequirementsProvider.getRequiredStructures() != null) {
                    for (RegistryKey<Structure> structure : goalRequirementsProvider.getRequiredStructures()) {
                        locateStructure(server, structure);
                        // if (data.isInRequiredDistance()) break; // only one needs to be found, and this is a time-expensive operation
                    }
                }
            }
            long end = System.currentTimeMillis();
            Lockout.log("Located " + BIOME_LOCATE_DATA.size() + " biomes and " + STRUCTURE_LOCATE_DATA.size() + " structures in " + String.format("%.2f", ((end-start)/1000.0)) + "s!");
        });
    }

    public static LocateData locateBiome(MinecraftServer server, RegistryKey<Biome> biome) {
        if (BIOME_LOCATE_DATA.containsKey(biome)) return BIOME_LOCATE_DATA.get(biome);

        // Lockout.log("Locating biome " + biome.getValue());

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

        // Lockout.log("Locating structure " + structure.getValue());

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
        PlayerManager playerManager = server.getPlayerManager();

        gameStartRunnables.clear();

        List<ServerPlayerEntity> allPlayers = new ArrayList<>();
        for (LockoutTeamServer team : teams) {
            for (UUID uuid : team.getPlayers()) {
                allPlayers.add(playerManager.getPlayer(uuid));
            }
        }

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
            serverPlayer.setOnFire(false);

            // Clear all stats
            for (@SuppressWarnings("unchecked") StatType<Object> statType : new StatType[]{Stats.CRAFTED, Stats.MINED, Stats.USED, Stats.BROKEN, Stats.PICKED_UP, Stats.DROPPED, Stats.KILLED, Stats.KILLED_BY, Stats.CUSTOM}) {
                for (Identifier id : statType.getRegistry().getIds()) {
                    serverPlayer.resetStat(statType.getOrCreateStat(statType.getRegistry().get(id)));
                }
            }
            serverPlayer.getStatHandler().sendStats(serverPlayer);
            serverPlayer.changeGameMode(GameMode.ADVENTURE);
        }
        ServerWorld world = server.getCommandSource().getWorld();
        world.setTimeOfDay(0);

        // Clear all advancements
        ServerPlayerEntity serverPlayerEntity;
        for(var it = allPlayers.iterator(); it.hasNext(); AdvancementCommand.Operation.REVOKE.processAll(serverPlayerEntity, server.getAdvancementLoader().getAdvancements())) {
            serverPlayerEntity = it.next();
        }


        BoardGenerator boardGenerator = new BoardGenerator(GoalRegistry.INSTANCE.getRegisteredGoals(), teams, availableDyeColors, BIOME_LOCATE_DATA, STRUCTURE_LOCATE_DATA);
        // Create lockout instance & generate board
        List<Pair<String,String>> board = boardGenerator.generateBoard();
        lockout = new Lockout(new LockoutBoard(board), teams);

        new CompassItemHandler(allPlayers);

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal instanceof HasTooltipInfo) {
                for (LockoutTeam team : lockout.getTeams()) {
                    ((LockoutTeamServer) team).sendLoreUpdate((Goal & HasTooltipInfo) goal);
                }
            }
        }

        PacketByteBuf buf = lockout.getTeamsGoalsPacket();
        for (ServerPlayerEntity player : allPlayers) {
            ServerPlayNetworking.send(player, Constants.LOCKOUT_GOALS_TEAMS_PACKET, buf);

            if (!lockout.isSoloBlackout()) {
                player.giveItemStack(CompassItemHandler.newCompass());
            }
        }


        for (int i = 3; i >= 0; i--) {
            if (i > 0) {
                int secs = i;
                ((LockoutRunnable) () -> {
                    playerManager.broadcast(Text.literal("Starting in " + secs + "..."), false);
                }).runTaskAfter(20 * (START_TIME - i));
            } else {
                ((LockoutRunnable) () -> {
                    lockout.setStarted(true);
                    lockout.setStartTime(System.currentTimeMillis());

                    for (ServerPlayerEntity player : allPlayers) {
                        if (player == null) continue;
                        ServerPlayNetworking.send(player, Constants.START_LOCKOUT_PACKET, lockout.getStartTimePacket());
                        player.changeGameMode(GameMode.SURVIVAL);
                    }
                    server.getPlayerManager().broadcast(Text.literal(lockout.getModeName() + " has begun."), false);
                }).runTaskAfter(20 * START_TIME);
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
        if (!Lockout.isLockoutRunning(lockout)) {
            context.getSource().sendError(Text.literal("There's no active lockout match."));
            return 0;
        }

        int idx = context.getArgument("goal number", Integer.class);

        Collection<GameProfile> gps = null;
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

        Goal goal = lockout.getBoard().getGoals().get(idx - 1);

        context.getSource().sendMessage(Text.of("Gave " + gp.getName() + " goal \"" + goal.getGoalName() + "\""));
        lockout.updateGoalCompletion(goal, gp.getId());
        return 1;
    }



}