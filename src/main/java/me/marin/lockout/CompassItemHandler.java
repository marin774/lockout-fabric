package me.marin.lockout;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class CompassItemHandler {

    public static CompassItemHandler INSTANCE;

    public List<UUID> players = new ArrayList<>();
    public Map<UUID, String> playerNames = new HashMap<>();
    public Map<UUID, Integer> currentSelection = new HashMap<>();

    public CompassItemHandler(List<ServerPlayerEntity> players) {
        INSTANCE = this;
        int i = 0;
        for (ServerPlayerEntity player : players) {
            this.players.add(player.getUuid());
            this.playerNames.put(player.getUuid(), player.getName().getString());

            if (i == 0) {
                this.currentSelection.put(player.getUuid(), 1);
            } else {
                this.currentSelection.put(player.getUuid(), 0);
            }
            i++;
        }
    }

    public void cycle(PlayerEntity player) {
        int cur = currentSelection.get(player.getUuid());
        int next = (cur+1) % players.size();
        if (players.get(next).equals(player.getUuid())) {
            next = (next+1) % players.size();
        }
        currentSelection.put(player.getUuid(), next);
    }



}
