package me.marin.lockout;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.PlayerManager;

import java.util.*;

public class CompassItemHandler {

    public static boolean isCompass(ItemStack item) {
        return item != null &&
                item.getItem() == Items.COMPASS &&
                Optional.ofNullable(item.get(DataComponentTypes.CUSTOM_DATA)).map(customData -> customData.contains("PlayerTracker")).orElse(false);
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
        NbtCompound compound = new NbtCompound();
        compound.putUuid("PlayerTracker", UUID.randomUUID());
        compass.set(DataComponentTypes.CUSTOM_DATA, NbtComponent.of(compound));
        return compass;
    }

}
