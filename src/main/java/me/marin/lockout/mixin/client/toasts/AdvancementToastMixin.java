package me.marin.lockout.mixin.client.toasts;

import me.marin.lockout.Lockout;
import me.marin.lockout.client.LockoutClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.toast.AdvancementToast;
import net.minecraft.client.toast.Toast;
import net.minecraft.client.toast.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AdvancementToast.class)
public class AdvancementToastMixin {

    @Inject(method = "draw", at = @At("HEAD"), cancellable = true)
    public void onDraw(DrawContext context, ToastManager manager, long startTime, CallbackInfoReturnable<Toast.Visibility> cir) {
        if (Lockout.exists(LockoutClient.lockout)) {
            cir.setReturnValue(Toast.Visibility.HIDE);
        }
    }

}
