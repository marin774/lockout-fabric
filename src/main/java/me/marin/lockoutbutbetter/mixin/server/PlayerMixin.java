package me.marin.lockoutbutbetter.mixin.server;

import me.marin.lockoutbutbetter.Lockout;
import me.marin.lockoutbutbetter.lockout.Goal;
import me.marin.lockoutbutbetter.lockout.interfaces.ConsumeItemGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraft.world.dimension.NetherPortal;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

    @Inject(method="eatFood", at = @At("HEAD"))
    public void onEat(World world, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;

        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (!(goal instanceof ConsumeItemGoal consumeItemGoal)) continue;

            if (goal.isCompleted()) continue;

            if (consumeItemGoal.getItem().equals(itemStack.getItem())) {
                lockout.completeGoal(goal, (PlayerEntity) (Object) this);
            }

        }

    }

}
