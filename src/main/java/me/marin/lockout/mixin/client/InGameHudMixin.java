package me.marin.lockout.mixin.client;

import me.marin.lockout.Lockout;
import me.marin.lockout.Utility;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.marin.lockout.Constants.*;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method="render", at=@At(
            value="INVOKE",
            target="Lnet/minecraft/client/gui/hud/DebugHud;shouldShowDebugHud()Z"))
    private void render(DrawContext context, float tickDelta, CallbackInfo ci) {
        // Show lockout screen
        if (!Lockout.exists()) {
            return;
        }

        int width = context.getScaledWindowWidth();

        Utility.drawBingoBoard(context, width - GUI_WIDTH, 0);
    }

}
