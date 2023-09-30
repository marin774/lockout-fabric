package me.marin.lockout.lockout.interfaces;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class WearArmorPieceGoal extends WearArmorGoal {

    public WearArmorPieceGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        List<Item> items = new ArrayList<>(getItems());
        for (ItemStack item : playerInventory.armor) {
            if (item == null) continue;
            if (items.remove(item.getItem())) {
                return true;
            }
        }
        return false;
    }

}
