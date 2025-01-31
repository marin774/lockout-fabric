package me.marin.lockout.mixin.server.network;

import com.mojang.authlib.GameProfile;
import me.marin.lockout.LockoutInitializer;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.network.packet.c2s.common.CommonPongC2SPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerCommonNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerCommonNetworkHandler.class)
public abstract class ServerCommonNetworkHandlerMixin {

    @Shadow protected abstract GameProfile getProfile();

    @Shadow @Final protected MinecraftServer server;

    @Inject(method = "onPong", at = @At("TAIL"))
    public void onPong(CommonPongC2SPacket packet, CallbackInfo ci) {
        server.execute(() -> {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(getProfile().getId());
            int id = packet.getParameter();

            if (LockoutServer.waitingForVersionPacketPlayersMap.containsKey(player)) {
                if (LockoutServer.waitingForVersionPacketPlayersMap.get(player) == id) {
                    player.networkHandler.disconnect(Text.of("Missing Lockout mod.\nServer is using Lockout v" + LockoutInitializer.MOD_VERSION.getFriendlyString() + "."));
                }
            }
        });
    }

}
