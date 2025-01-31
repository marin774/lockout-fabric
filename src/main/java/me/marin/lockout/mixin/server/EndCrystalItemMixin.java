package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.obtain.PlaceEndCrystalGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EndCrystalItem.class)
public class EndCrystalItemMixin {

    @Inject(method = "useOnBlock", at = @At("RETURN"))
    public void m(ItemUsageContext context, CallbackInfoReturnable<ActionResult> cir) {
        if (context.getWorld().isClient) return;
        if (cir.getReturnValue() != ActionResult.SUCCESS) return;

        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof PlaceEndCrystalGoal) {
                lockout.completeGoal(goal, context.getPlayer());
                return;
            }
        }

    }

}
