package me.marin.lockout.mixin.client;

import me.marin.lockout.Lockout;
import me.marin.lockout.client.LockoutClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import net.minecraft.util.math.Vec2f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class InputMixin extends Input {

    @Inject(method ="tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        if (!Lockout.isLockoutRunning(LockoutClient.lockout)) return;
        if (!LockoutClient.amIPlayingLockout) return;

        KeyboardInput input = (KeyboardInput) (Object) this;
        if (!LockoutClient.lockout.hasStarted()) {
            input.playerInput = new PlayerInput(false, false, false, false, false, input.playerInput.sneak(), false);
            movementVector = new Vec2f(0, 0);
        }
    }

}
