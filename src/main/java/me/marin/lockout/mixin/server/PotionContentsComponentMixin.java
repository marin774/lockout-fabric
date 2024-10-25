package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.DrinkPotionGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.component.type.ConsumableComponent;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PotionContentsComponent.class)
public class PotionContentsComponentMixin {

    @Inject(method = "onConsume", at = @At("HEAD"))
    public void onConsume(World world, LivingEntity user, ItemStack stack, ConsumableComponent consumable, CallbackInfo ci) {
        if (!(user instanceof PlayerEntity player)) return;
        if (player.getWorld().isClient) return;

        PotionContentsComponent potionContents = (PotionContentsComponent) (Object) this;

        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof DrinkPotionGoal drinkPotionGoal && potionContents.potion().isPresent() && drinkPotionGoal.getPotion().equals(potionContents.potion().get())) {
                lockout.completeGoal(goal, player);
            }
        }
    }

}
