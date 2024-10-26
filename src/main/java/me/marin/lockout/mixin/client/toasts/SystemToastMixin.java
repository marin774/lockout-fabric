package me.marin.lockout.mixin.client.toasts;

import me.marin.lockout.Lockout;
import me.marin.lockout.client.LockoutClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.SystemToast;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SystemToast.class)
public class SystemToastMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    public void onDraw(DrawContext context, TextRenderer textRenderer, long startTime, CallbackInfo ci) {
        if (Lockout.exists(LockoutClient.lockout)) {
            ci.cancel();
        }
    }

}
