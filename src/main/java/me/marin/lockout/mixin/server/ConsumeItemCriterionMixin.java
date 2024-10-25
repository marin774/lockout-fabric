package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.status_effect.RemoveStatusEffectUsingMilkGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.advancement.criterion.ConsumeItemCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConsumeItemCriterion.class)
public class ConsumeItemCriterionMixin {
    @Inject(method="trigger", at = @At("HEAD"))
    public void trigger(ServerPlayerEntity player, ItemStack stack, CallbackInfo ci) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof RemoveStatusEffectUsingMilkGoal) {
                if (!player.getStatusEffects().isEmpty() && stack.getItem().equals(Items.MILK_BUCKET)) {
                    lockout.completeGoal(goal, player);
                    return;
                }
            }
        }
    }
}
