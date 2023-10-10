package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.workstation.UseLoomGoal;
import me.marin.lockout.lockout.goals.workstation.UseStonecutterGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method="onTakeItem", at = @At("HEAD"))
    public void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (player.getWorld().isClient) return;
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof UseStonecutterGoal) {
                if (player.currentScreenHandler instanceof StonecutterScreenHandler stonecutterScreenHandler) {
                    if ((Object) this == stonecutterScreenHandler.slots.get(1)) {
                        lockout.completeGoal(goal, player);
                    }
                }
            }

            if (goal instanceof UseLoomGoal) {
                if (player.currentScreenHandler instanceof LoomScreenHandler loomScreenHandler) {
                    if ((Object) this == loomScreenHandler.getOutputSlot()) {
                        lockout.completeGoal(goal, player);
                    }
                }
            }

        }
    }

}
