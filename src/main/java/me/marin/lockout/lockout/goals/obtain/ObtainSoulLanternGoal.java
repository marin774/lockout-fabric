package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class ObtainSoulLanternGoal extends ObtainAllItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.SOUL_LANTERN);

    public ObtainSoulLanternGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain Soul Lantern";
    }

}
