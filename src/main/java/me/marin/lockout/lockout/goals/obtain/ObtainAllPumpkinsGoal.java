package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class ObtainAllPumpkinsGoal extends ObtainAllItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.PUMPKIN, Items.CARVED_PUMPKIN, Items.JACK_O_LANTERN);

    public ObtainAllPumpkinsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain every type of Pumpkin";
    }

}
