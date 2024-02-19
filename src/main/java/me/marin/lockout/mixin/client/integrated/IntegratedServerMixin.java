package me.marin.lockout.mixin.client.integrated;

import me.marin.lockout.server.LockoutServer;
import net.minecraft.server.integrated.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(method = "setupServer", at = @At("RETURN"))
    public void onServerSetup(CallbackInfoReturnable<Boolean> cir) {
        // world done loading
        if (!cir.getReturnValueZ()) return;

        IntegratedServer server = (IntegratedServer) (Object) this;

        LockoutServer.initializeServer();
    }

}
