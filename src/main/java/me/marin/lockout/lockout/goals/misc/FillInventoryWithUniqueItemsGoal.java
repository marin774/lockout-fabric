package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.lockout.interfaces.ObtainItemsGoal;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FillInventoryWithUniqueItemsGoal extends ObtainItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.CHEST);
    public FillInventoryWithUniqueItemsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Fill Inventory with unique items";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        Set<Item> itemTypes = new HashSet<>();
        for (ItemStack item : playerInventory.main) {
            if (item == null || item.isEmpty()) return false;

            if (!itemTypes.add(item.getItem())) return false;
        }
        return (itemTypes.size() == 36);
    }

}
