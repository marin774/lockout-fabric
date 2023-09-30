package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class WearGoldenArmorGoal extends WearArmorGoal {

    private static final List<Item> ITEMS = List.of(Items.GOLDEN_HELMET, Items.GOLDEN_CHESTPLATE, Items.GOLDEN_LEGGINGS, Items.GOLDEN_BOOTS);

    public WearGoldenArmorGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear Full Golden Armor";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

}
