package me.marin.lockout;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import me.marin.lockout.server.LockoutServer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.*;

public class LockoutTeamServer extends LockoutTeam {

    private final List<UUID> players = new ArrayList<>();
    private final Map<UUID, String> playerNameMap = new HashMap<>();
    private final MinecraftServer server;

    public LockoutTeamServer(List<String> playerNames, Formatting formattingColor, MinecraftServer server) {
        super(playerNames, formattingColor);
        this.server = server;

        PlayerManager manager = server.getPlayerManager();

        // All players from playerNames are online at this moment.
        for (String playerName : playerNames) {
            this.players.add(manager.getPlayer(playerName).getUuid());
            this.playerNameMap.put(manager.getPlayer(playerName).getUuid(), playerName);
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    public List<UUID> getPlayers() {
        return players;
    }

    public String getPlayerName(UUID uuid) {
        return playerNameMap.get(uuid);
    }

    public void sendMessage(String message) {
        for (UUID uuid : players) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(uuid);
            if (player != null) {
                player.sendMessage(Text.literal(message));
            }
        }
    }

    public <T extends Goal & HasTooltipInfo> void sendTooltipUpdate(T goal) {
        sendTooltipUpdate(goal, true);
    }
    public <T extends Goal & HasTooltipInfo> void sendTooltipUpdate(T goal, boolean updateSpectators) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(goal.getId());
        buf.writeString(String.join("\n", goal.getTooltip(this)));
        this.sendPacket(Constants.UPDATE_TOOLTIP, buf);

        if (updateSpectators) {
            this.sendTooltipPacketSpectators(goal);
        }
    }
    public void sendPacket(Identifier packetId, PacketByteBuf packet) {
        for (UUID playerId : players) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
            if (player != null) {
                ServerPlayNetworking.send(player, packetId, packet);
            }
        }
    }
    private <T extends Goal & HasTooltipInfo> void sendTooltipPacketSpectators(T goal) {
        PacketByteBuf specBuf = PacketByteBufs.create();
        specBuf.writeString(goal.getId());
        specBuf.writeString(String.join("\n", goal.getSpectatorTooltip()));
        for (ServerPlayerEntity spectator : Utility.getSpectators(LockoutServer.lockout, server)) {
            ServerPlayNetworking.send(spectator, Constants.UPDATE_TOOLTIP, specBuf);
        }
    }

}
