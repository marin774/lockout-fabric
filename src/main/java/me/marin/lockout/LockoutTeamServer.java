package me.marin.lockout;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LockoutTeamServer extends LockoutTeam {

    private final List<UUID> players = new ArrayList<>();
    private final MinecraftServer server;

    public LockoutTeamServer(List<String> playerNames, Formatting formattingColor, MinecraftServer server) {
        super(playerNames, formattingColor);
        this.server = server;

        // All players from playerNames are online at this moment.
        for (String playerName : playerNames) {
            this.players.add(server.getPlayerManager().getPlayer(playerName).getUuid());
        }
    }

    public MinecraftServer getServer() {
        return server;
    }

    public List<UUID> getPlayers() {
        return players;
    }


}
