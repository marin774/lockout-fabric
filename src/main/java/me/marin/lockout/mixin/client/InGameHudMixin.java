package me.marin.lockout.mixin.client;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutConfig;
import me.marin.lockout.Utility;
import me.marin.lockout.client.LockoutClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.marin.lockout.Constants.GUI_PADDING;
import static me.marin.lockout.Constants.GUI_SLOT_SIZE;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    // Render the board after effects, but before chat, player list etc.
    @Inject(method = "render", at = @At(value="INVOKE", target = "Lnet/minecraft/client/gui/hud/InGameHud;renderSleepOverlay(Lnet/minecraft/client/gui/DrawContext;Lnet/minecraft/client/render/RenderTickCounter;)V", shift = At.Shift.AFTER))
    public void renderBoard(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!Lockout.exists(LockoutClient.lockout)) {
            return;
        }

        Utility.drawBingoBoard(context);
    }

    // If lockout board is visible, render effects to the left of it.
    @Redirect(method = "renderStatusEffectOverlay", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;getScaledWindowWidth()I"))
    private int renderStatusEffects(DrawContext instance) {
        int width = instance.getScaledWindowWidth();

        if (!Lockout.exists(LockoutClient.lockout)) {
            return width;
        }

        if (LockoutConfig.getInstance().boardPosition != LockoutConfig.BoardPosition.RIGHT) {
            return width;
        }

        return width - 2 * GUI_PADDING - LockoutClient.lockout.getBoard().size() * GUI_SLOT_SIZE;
    }

}
