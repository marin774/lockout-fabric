package me.marin.lockout.server.handlers;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.EnterDimensionGoal;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import static me.marin.lockout.server.LockoutServer.lockout;

public class AfterPlayerChangeWorldEventHandler implements ServerEntityWorldChangeEvents.AfterPlayerChange {

    @Override
    public void afterChangeWorld(ServerPlayerEntity player, ServerWorld origin, ServerWorld destination) {
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (!(goal instanceof EnterDimensionGoal enterDimensionGoal)) continue;
            if (goal.isCompleted()) continue;

            if (destination.getRegistryKey() == enterDimensionGoal.getWorldRegistryKey()) {
                lockout.completeGoal(goal, player);
            }
        }
    }

}
