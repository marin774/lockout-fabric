package me.marin.lockout.mixin.server;

import com.llamalad7.mixinextras.sugar.Local;
import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.ShootFireworkFromCrossbowGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CrossbowItem.class)
public class CrossbowItemMixin {

    @Inject(method = "use", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/CrossbowItem;shootAll(Lnet/minecraft/world/World;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;FFLnet/minecraft/entity/LivingEntity;)V"))
    public void onShoot(World world, PlayerEntity user, Hand hand, CallbackInfoReturnable<ActionResult> cir, @Local ChargedProjectilesComponent chargedProjectilesComponent) {
        if (world.isClient) return;

        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        boolean hasRocket = chargedProjectilesComponent.contains(Items.FIREWORK_ROCKET);
        if (!hasRocket) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof ShootFireworkFromCrossbowGoal) {
                lockout.completeGoal(goal, user);
                return;
            }
        }
    }

}
