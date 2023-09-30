package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.workstation.UseAnvilGoal;
import me.marin.lockout.lockout.goals.workstation.UseGrindstoneGoal;
import me.marin.lockout.lockout.goals.workstation.UseLoomGoal;
import me.marin.lockout.lockout.goals.workstation.UseStonecutterGoal;
import me.marin.lockout.lockout.interfaces.IncrementStatGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.*;
import net.minecraft.screen.slot.Slot;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method="onTakeItem", at = @At("HEAD"))
    public void onTakeItem(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        player.sendMessage(Text.of("Take item " + stack.toString()));
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();

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

            if (goal instanceof UseGrindstoneGoal) {
                if (player.currentScreenHandler instanceof GrindstoneScreenHandler grindstoneScreenHandler) {
                    player.sendMessage(Text.literal("Index: " + grindstoneScreenHandler.slots.indexOf((Slot) (Object) this)));
                    if ((Object) this == grindstoneScreenHandler.slots.get(2)) {
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
