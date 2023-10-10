package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.ObtainPotionItemGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.advancement.criterion.BrewedPotionCriterion;
import net.minecraft.potion.Potion;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BrewedPotionCriterion.class)
public class BrewedPotionCriterionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onTrigger(ServerPlayerEntity player, Potion potion, CallbackInfo ci) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof ObtainPotionItemGoal obtainPotionItemGoal) {
                if (obtainPotionItemGoal.getPotions().contains((potion))) {
                    lockout.completeGoal(obtainPotionItemGoal, player);
                }
            }
        }
    }

}
