package me.marin.lockoutbutbetter.lockout.goals.obtain;

import me.marin.lockoutbutbetter.lockout.interfaces.ObtainItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class ObtainWoodenTools extends ObtainItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.WOODEN_AXE, Items.WOODEN_HOE, Items.WOODEN_PICKAXE, Items.WOODEN_SWORD, Items.WOODEN_SHOVEL);

    public ObtainWoodenTools(String id) {
        super(id);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain Wooden Tools";
    }

}
