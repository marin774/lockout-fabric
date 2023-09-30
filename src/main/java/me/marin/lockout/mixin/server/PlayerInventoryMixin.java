package me.marin.lockout.mixin.server;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(PlayerInventory.class)
public class PlayerInventoryMixin {

    /*
    @Shadow @Final
    public PlayerEntity player;

    private static ItemStack inserted;

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("HEAD"))
    public void onInsertStackHead(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        inserted = stack.copy();
    }

    @Inject(method = "insertStack(ILnet/minecraft/item/ItemStack;)Z", at = @At("RETURN"))
    public void onInsertStackReturn(int slot, ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;
        if (!cir.getReturnValueZ()) return;

        obtainedItem(player, inserted);
    }

    @Inject(method="setStack", at = @At("TAIL"))
    public void onSetStack(int slot, ItemStack stack, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        // TODO: this can be used for armor/offhand too

        obtainedItem(player, stack);
    }

    private void obtainedItem(PlayerEntity player, ItemStack stack) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (!(goal instanceof ObtainItemsGoal obtainItemGoal)) continue;
            if (goal.isCompleted()) continue;

            if (obtainItemGoal.getItems().equals(stack.getItem())) {
                lockout.completeGoal(goal, player);
            }
        }
    }
     */

}
