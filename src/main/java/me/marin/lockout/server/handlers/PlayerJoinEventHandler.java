package me.marin.lockout.server.handlers;

import me.marin.lockout.LockoutInitializer;
import me.marin.lockout.network.LockoutVersionPayload;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.packet.s2c.common.CommonPingS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.marin.lockout.server.LockoutServer.waitingForVersionPacketPlayersMap;

public class PlayerJoinEventHandler implements ServerPlayConnectionEvents.Join {
    @Override
    public void onPlayReady(ServerPlayNetworkHandler handler, PacketSender packetSender, MinecraftServer minecraftServer) {
        // Check if the client has the correct mod version:
        // 1. Send the Lockout version packet
        // 2. Follow it with a Minecraft ping packet (id is hash of 'username + version' string)
        // 3. Wait for the packets.
        // 4. If ping response is received before the version response, they don't have the mod
        // 5. Otherwise, compare the versions, and kick them if needed.

        ServerPlayerEntity player = handler.getPlayer();
        int id = (player.getName().getString() + LockoutInitializer.MOD_VERSION.getFriendlyString()).hashCode();

        ServerPlayNetworking.send(player, new LockoutVersionPayload(LockoutInitializer.MOD_VERSION.getFriendlyString()));
        player.networkHandler.sendPacket(new CommonPingS2CPacket(id));

        waitingForVersionPacketPlayersMap.put(player, id);
    }
}
