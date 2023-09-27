package me.marin.lockoutbutbetter.lockout.goals.obtain;

import me.marin.lockoutbutbetter.lockout.interfaces.ObtainItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class ObtainDiamondTools extends ObtainItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.DIAMOND_AXE, Items.DIAMOND_HOE, Items.DIAMOND_PICKAXE, Items.DIAMOND_SWORD, Items.DIAMOND_SHOVEL);

    public ObtainDiamondTools(String id) {
        super(id);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain Diamond Tools";
    }

}
