package me.marin.lockoutbutbetter.mixin.client;

import me.marin.lockoutbutbetter.Constants;
import me.marin.lockoutbutbetter.Lockout;
import me.marin.lockoutbutbetter.Utility;
import me.marin.lockoutbutbetter.client.LockoutBoard;
import me.marin.lockoutbutbetter.lockout.Goal;
import me.marin.lockoutbutbetter.lockout.texture.CustomTextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.marin.lockoutbutbetter.Constants.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method="render", at=@At(
            value="INVOKE",
            target="Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Show lockout screen
        if (!Lockout.isRunning()) {
            return;
        }

        int width = context.getScaledWindowWidth();

        Utility.drawBingoBoard(context, width - GUI_WIDTH, 0);
    }

}
