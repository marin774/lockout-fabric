package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class ObtainAllHorseArmorGoal extends ObtainAllItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.IRON_HORSE_ARMOR, Items.LEATHER_HORSE_ARMOR, Items.DIAMOND_HORSE_ARMOR, Items.GOLDEN_HORSE_ARMOR);

    public ObtainAllHorseArmorGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain every type of Horse Armor";
    }

}
