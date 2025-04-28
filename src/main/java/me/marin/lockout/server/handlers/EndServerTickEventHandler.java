package me.marin.lockout.server.handlers;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutRunnable;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.have_more.HaveMostXPLevelsGoal;
import me.marin.lockout.lockout.goals.misc.EmptyHungerBarGoal;
import me.marin.lockout.lockout.goals.misc.ReachBedrockGoal;
import me.marin.lockout.lockout.goals.misc.ReachHeightLimitGoal;
import me.marin.lockout.lockout.goals.misc.ReachNetherRoofGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentTouchesWaterGoal;
import me.marin.lockout.lockout.interfaces.ObtainItemsGoal;
import me.marin.lockout.lockout.interfaces.OpponentObtainsItemGoal;
import me.marin.lockout.lockout.interfaces.RideEntityGoal;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SaddledComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.HashSet;
import java.util.Objects;

import static me.marin.lockout.server.LockoutServer.gameStartRunnables;
import static me.marin.lockout.server.LockoutServer.lockout;

public class EndServerTickEventHandler implements ServerTickEvents.EndTick {

    @Override
    public void onEndTick(MinecraftServer server) {
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (LockoutRunnable runnable : new HashSet<>(gameStartRunnables.keySet())) {
            if (gameStartRunnables.get(runnable) <= 0) {
                runnable.run();
                gameStartRunnables.remove(runnable);
            } else {
                gameStartRunnables.merge(runnable, -1L, Long::sum);
            }
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
                        if (Objects.equals(vehicle, EntityType.PIG)) {
                            boolean hasCarrotOnAStick = false;
                            var handItem = player.getInventory().getSelectedStack();
                            if (handItem.getItem().equals(Items.CARROT_ON_A_STICK)) {
                                hasCarrotOnAStick = true;
                            }
                            allow = hasCarrotOnAStick;
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
    }
}
