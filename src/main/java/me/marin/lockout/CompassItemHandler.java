package me.marin.lockout;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;

import java.util.*;

@Environment(EnvType.SERVER)
public class CompassItemHandler {

    public static boolean isCompass(ItemStack item) {
        return item != null && item.getItem() == Items.COMPASS && item.hasNbt() && item.getNbt().contains("PlayerTracker");
    }

    public final List<UUID> players = new ArrayList<>();
    public final Map<UUID, String> playerNames = new HashMap<>();
    public final Map<UUID, Integer> currentSelection = new HashMap<>();
    public final Map<UUID, Integer> compassSlots = new HashMap<>();

    public CompassItemHandler(List<UUID> players, PlayerManager playerManager) {
        for (int i = 0; i < players.size(); i++) {
            UUID playerId = players.get(i);
            this.players.add(playerId);
            this.playerNames.put(playerId, playerManager.getPlayer(playerId).getName().getString());

            this.currentSelection.put(playerId, i == 0 ? 1 : 0);
        }
    }

    public void cycle(PlayerEntity player) {
        int cur = currentSelection.get(player.getUuid());
        int next = (cur + 1) % players.size();
        if (players.get(next).equals(player.getUuid())) {
            next = (next + 1) % players.size();
        }
        currentSelection.put(player.getUuid(), next);
    }

    public ItemStack newCompass() {
        ItemStack compass = Items.COMPASS.getDefaultStack();
        NbtCompound compound = compass.getOrCreateNbt();
        compound.putUuid("PlayerTracker", UUID.randomUUID());
        compass.setNbt(compound);
        return compass;
    }

}
