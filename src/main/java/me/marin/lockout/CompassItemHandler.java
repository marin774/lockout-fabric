package me.marin.lockout;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.*;

public class CompassItemHandler {

    public static CompassItemHandler INSTANCE;
    public static ItemStack newCompass() {
        ItemStack compass = Items.COMPASS.getDefaultStack();
        NbtCompound compound = compass.getOrCreateNbt();
        compound.putUuid("PlayerTracker", UUID.randomUUID());
        compass.setNbt(compound);
        return compass;
    }
    public static boolean isCompass(ItemStack item) {
        return item != null && item.getItem() == Items.COMPASS && item.hasNbt() && item.getNbt().contains("PlayerTracker");
    }

    public List<UUID> players = new ArrayList<>();
    public Map<UUID, String> playerNames = new HashMap<>();
    public Map<UUID, Integer> currentSelection = new HashMap<>();

    public CompassItemHandler(List<ServerPlayerEntity> players) {
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
