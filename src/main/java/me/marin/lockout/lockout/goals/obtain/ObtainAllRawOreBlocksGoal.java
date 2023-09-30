package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;

public class ObtainAllRawOreBlocksGoal extends ObtainAllItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.RAW_COPPER_BLOCK, Items.RAW_GOLD_BLOCK, Items.RAW_IRON_BLOCK);

    public ObtainAllRawOreBlocksGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain every type of Raw Ore Block";
    }

}
