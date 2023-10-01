package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.workstation.UseSmithingTableGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.SmithingScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public class SmithingScreenHandlerMixin {

    @Inject(method = "onTakeOutput", at = @At("TAIL"))
    public void onTakeOutputMixin(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof UseSmithingTableGoal) {
                lockout.completeGoal(goal, player);
            }
        }
    }

}
