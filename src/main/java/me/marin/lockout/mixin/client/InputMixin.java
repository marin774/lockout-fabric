package me.marin.lockout.mixin.client;

import me.marin.lockout.Lockout;
import net.minecraft.client.input.KeyboardInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class InputMixin {

    @Inject(method ="tick", at = @At("TAIL"))
    public void tick(boolean slowDown, float slowDownFactor, CallbackInfo ci) {
        if (!Lockout.isLockoutRunning()) return;
        Lockout lockout = Lockout.getInstance();
        KeyboardInput input = (KeyboardInput) (Object) this;
        if (!lockout.hasStarted()) {
            input.pressingForward = false;
            input.pressingBack = false;
            input.pressingLeft = false;
            input.pressingRight = false;
            input.movementForward = 0;
            input.movementSideways = 0;
            input.jumping = false;
            // input.sneaking = false;
        }
    }

}
