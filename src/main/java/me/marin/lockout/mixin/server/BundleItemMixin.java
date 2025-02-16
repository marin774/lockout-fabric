package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.FillBundleWithBundlesGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.StackReference;
import net.minecraft.item.BundleItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.Slot;
import net.minecraft.util.ClickType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BundleItem.class)
public class BundleItemMixin {

    @Inject(method = "onClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BundleItem;onContentChanged(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public void onClicked(ItemStack stack, ItemStack otherStack, Slot slot, ClickType clickType, PlayerEntity player, StackReference cursorStackReference, CallbackInfoReturnable<Boolean> cir) {
        if (player.getWorld().isClient) return;

        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        lockout$onClick(lockout, player, stack);
    }

    @Inject(method = "onStackClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BundleItem;onContentChanged(Lnet/minecraft/entity/player/PlayerEntity;)V"))
    public void onStackClicked(ItemStack stack, Slot slot, ClickType clickType, PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player.getWorld().isClient) return;

        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        lockout$onClick(lockout, player, stack);
    }

    @Unique
    private static void lockout$onClick(Lockout lockout, PlayerEntity player, ItemStack stack) {
        BundleContentsComponent bcc = stack.get(DataComponentTypes.BUNDLE_CONTENTS);

        int bundles = 0;
        loop:
        for (int i = 0; i < bcc.size(); i++) {
            for (BundleItem bundle : BundleItem.getBundles()) {
                if (bcc.get(i).isOf(bundle)) {
                    bundles += 1;
                    continue loop;
                }
            }
            return;
        }
        if (bundles < 16) {
            return;
        }

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof FillBundleWithBundlesGoal) {
                lockout.completeGoal(goal, player);
            }
        }
    }

}
