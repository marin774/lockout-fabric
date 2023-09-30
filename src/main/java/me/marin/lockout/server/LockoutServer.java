package me.marin.lockout.server;

import me.marin.lockout.Constants;
import me.marin.lockout.Lockout;
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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
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
import net.minecraft.server.PlayerManager;
import net.minecraft.server.command.AdvancementCommand;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.world.GameMode;
import net.minecraft.world.dimension.DimensionTypes;
import oshi.util.tuples.Pair;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;

public class LockoutServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var commandNode = literal("lockout").executes(context -> {
                PacketByteBuf buf = PacketByteBufs.create();
                List<Pair<String, String>> goals = List.of(
                        new Pair<>(GoalType.USE_GRINDSTONE, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.USE_LOOM, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.USE_SMITHING_TABLE, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.EAT_CAKE, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.DIE_BY_TNT_MINECART, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.USE_CAULDRON, GoalDataConstants.DATA_NONE),
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
                        new Pair<>(GoalType.USE_STONECUTTER, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.USE_ANVIL, GoalDataConstants.DATA_NONE),
                        new Pair<>(GoalType.USE_ENCHANTING_TABLE, GoalDataConstants.DATA_NONE)
                );
                for (var pair : goals) {
                    buf.writeString(pair.getA());
                    buf.writeString(pair.getB());
                }

                PlayerManager playerManager = context.getSource().getServer().getPlayerManager();

                for (ServerPlayerEntity serverPlayer : playerManager.getPlayerList()) {
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
                for(Iterator<ServerPlayerEntity> playerIterator = playerManager.getPlayerList().iterator(); playerIterator.hasNext(); AdvancementCommand.Operation.REVOKE.processAll(serverPlayerEntity, context.getSource().getServer().getAdvancementLoader().getAdvancements())) {
                    serverPlayerEntity = playerIterator.next();
                }
                Lockout lockout = new Lockout(new LockoutBoard(goals));

                ServerPlayNetworking.send((ServerPlayerEntity) context.getSource().getEntity(), Constants.BEGIN_LOCKOUT_PACKET, buf);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lockout has begun."), false);

                return 1;
            }).build();

            dispatcher.getRoot().addChild(commandNode);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            if (!Lockout.isRunning()) return;

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

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (!Lockout.isRunning()) return;

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
            if (!Lockout.isRunning()) return;

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

}
