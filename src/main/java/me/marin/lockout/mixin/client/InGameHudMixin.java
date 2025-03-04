package me.marin.lockout.mixin.client;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutConfig;
import me.marin.lockout.client.LockoutClient;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import static me.marin.lockout.Constants.GUI_PADDING;
import static me.marin.lockout.Constants.GUI_SLOT_SIZE;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    // If lockout board is visible, render effects to the left of it.
    @ModifyArg(method="renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawGuiTexture(Ljava/util/function/Function;Lnet/minecraft/util/Identifier;IIII)V"), index = 2)
    private int renderStatusEffectOverlay_drawGuiTexture(int width) {
        if (!Lockout.exists(LockoutClient.lockout)) {
            return width;
        }

        if (LockoutConfig.getInstance().boardSide != LockoutConfig.BoardSide.RIGHT) {
            return width;
        }

        return width - 2 * GUI_PADDING - LockoutClient.lockout.getBoard().size() * GUI_SLOT_SIZE;
    }
    @ModifyArg(method="method_18620", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawSpriteStretched(Ljava/util/function/Function;Lnet/minecraft/client/texture/Sprite;IIIII)V"), index = 2)
    private static int renderStatusEffectOverlay_drawSpriteStretched(int width) {
        if (!Lockout.exists(LockoutClient.lockout)) {
            return width;
        }

        if (LockoutConfig.getInstance().boardSide != LockoutConfig.BoardSide.RIGHT) {
            return width;
        }

        return width - 2 * GUI_PADDING - LockoutClient.lockout.getBoard().size() * GUI_SLOT_SIZE;
    }



}
