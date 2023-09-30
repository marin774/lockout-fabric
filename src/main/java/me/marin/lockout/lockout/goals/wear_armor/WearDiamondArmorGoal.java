package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class WearDiamondArmorGoal extends WearArmorGoal {

    private static final List<Item> ITEMS = List.of(Items.DIAMOND_HELMET, Items.DIAMOND_CHESTPLATE, Items.DIAMOND_LEGGINGS, Items.DIAMOND_BOOTS);

    public WearDiamondArmorGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear Full Diamond Armor";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

}
