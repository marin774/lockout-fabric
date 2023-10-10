package me.marin.lockout.mixin.client.integrated;

import me.marin.lockout.server.LockoutServer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.server.SaveLoader;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.server.integrated.IntegratedServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.ApiServices;
import net.minecraft.world.level.storage.LevelStorage;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {

    @Inject(method= "<init>", at = @At("TAIL"))
    public void onInit(Thread serverThread, MinecraftClient client, LevelStorage.Session session, ResourcePackManager dataPackManager, SaveLoader saveLoader, ApiServices apiServices, WorldGenerationProgressListenerFactory worldGenerationProgressListenerFactory, CallbackInfo ci) {
        // integrated server initialized, world hasn't started loading yet
    }

    @Inject(method = "setupServer", at = @At("RETURN"))
    public void onServerSetup(CallbackInfoReturnable<Boolean> cir) {
        // world done loading
        if (!cir.getReturnValueZ()) return;

        IntegratedServer server = (IntegratedServer) (Object) this;

        LockoutServer.initializeServer();
    }

}
