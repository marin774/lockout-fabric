package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.mixin.server.PlayerInventoryAccessor;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * Checks the entire inventory (including off-hand and armor slots) for Item types
 */
public abstract class ObtainAllItemsGoal extends ObtainItemsGoal {

    public ObtainAllItemsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        List<Item> items = new ArrayList<>(getItems());
        for (var defaultedList : ((PlayerInventoryAccessor) playerInventory).getCombinedInventory()) {
            for (ItemStack item : defaultedList) {
                if (item == null) continue;

                boolean allow = true;
                if (this instanceof RequiresAmount requiresAmount) {
                    allow = playerInventory.count(item.getItem()) >= requiresAmount.getAmount();
                }
                if (allow && items.remove(item.getItem())) {
                    if (items.isEmpty()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

}
