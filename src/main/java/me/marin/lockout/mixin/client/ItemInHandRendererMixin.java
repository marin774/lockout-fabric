package me.marin.lockout.mixin.client;

import me.marin.lockout.CompassItemHandler;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "applyEquipOffset", at = @At("HEAD"), cancellable = true)
    public void render(MatrixStack matrices, Arm arm, float equipProgress, CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player == null) return;
        ItemStack stack = arm == player.getMainArm() ? player.getMainHandStack() : player.getOffHandStack();
        if (!CompassItemHandler.isCompass(stack)) return;

        ci.cancel();
        int i = arm == Arm.RIGHT ? 1 : -1;
        matrices.translate((float)i * 0.56F, -0.52F + 0 * -0.6F, -0.72F);
    }

}
