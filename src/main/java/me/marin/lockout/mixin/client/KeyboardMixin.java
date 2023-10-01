package me.marin.lockout.mixin.client;

import net.minecraft.client.Keyboard;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Keyboard.class)
public class KeyboardMixin {
/*
    private Keyboard thiz = (Keyboard) (Object) this;

     @Inject(method = "processF3", at = @At("HEAD"))
    public void onProcessF3(int key, CallbackInfoReturnable<Boolean> cir) {
        if (!(thiz.debugCrashStartTime > 0L && thiz.debugCrashStartTime < Util.getMeasuringTimeMs() - 100L)) {
            if (key == InputUtil.GLFW_KEY_LEFT_SHIFT || key == InputUtil.GLFW_KEY_RIGHT) {
                thiz.client.getDebugHud().toggleRenderingChart();
            }
        }
    }*/

}
