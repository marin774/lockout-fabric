package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class WearIronArmorGoal extends WearArmorGoal {

    private static final List<Item> ITEMS = List.of(Items.IRON_HELMET, Items.IRON_CHESTPLATE, Items.IRON_LEGGINGS, Items.IRON_BOOTS);

    public WearIronArmorGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear Full Iron Armor";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

}
