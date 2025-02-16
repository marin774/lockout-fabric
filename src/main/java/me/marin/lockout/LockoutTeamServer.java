package me.marin.lockout;

import lombok.Getter;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import me.marin.lockout.network.UpdateTooltipPayload;
import me.marin.lockout.server.LockoutServer;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.*;

public class LockoutTeamServer extends LockoutTeam {

    @Getter
    private final List<UUID> players = new ArrayList<>();
    private final Map<UUID, String> playerNameMap = new HashMap<>();
    @Getter
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
        var payload = new UpdateTooltipPayload(goal.getId(), String.join("\n", goal.getTooltip(this)));
        for (UUID playerId : players) {
            ServerPlayerEntity player = server.getPlayerManager().getPlayer(playerId);
            if (player != null) {
                ServerPlayNetworking.send(player, payload);
            }
        }

        if (updateSpectators) {
            this.sendTooltipPacketSpectators(goal);
        }
    }
    private <T extends Goal & HasTooltipInfo> void sendTooltipPacketSpectators(T goal) {
        var payload = new UpdateTooltipPayload(goal.getId(), String.join("\n", goal.getSpectatorTooltip()));
        for (ServerPlayerEntity spectator : Utility.getSpectators(LockoutServer.lockout, server)) {
            ServerPlayNetworking.send(spectator, payload);
        }
    }

}
