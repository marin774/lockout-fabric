package me.marin.lockout;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class LockoutTeamServer extends LockoutTeam {

    private final List<ServerPlayerEntity> players = new ArrayList<>();

    public LockoutTeamServer(List<String> playerNames, Formatting formattingColor, MinecraftServer server) {
        super(playerNames, formattingColor);

        // All players from playerNames are online at this moment.
        for (String playerName : playerNames) {
            this.players.add(server.getPlayerManager().getPlayer(playerName));
        }
    }

    public List<ServerPlayerEntity> getPlayers() {
        return players;
    }


}
