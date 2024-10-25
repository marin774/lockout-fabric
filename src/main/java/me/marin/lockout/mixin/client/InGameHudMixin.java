package me.marin.lockout.mixin.client;

import me.marin.lockout.Lockout;
import me.marin.lockout.Utility;
import me.marin.lockout.client.LockoutClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.marin.lockout.Constants.GUI_WIDTH;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Inject(method="render", at=@At(
            value="FIELD",
            target="Lnet/minecraft/client/gui/hud/InGameHud;layeredDrawer:Lnet/minecraft/client/gui/LayeredDrawer;"))
    private void render(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Show lockout screen
        if (!Lockout.exists(LockoutClient.lockout)) {
            return;
        }

        int width = context.getScaledWindowWidth();

        Utility.drawBingoBoard(context, width - GUI_WIDTH, 0);
    }

}
