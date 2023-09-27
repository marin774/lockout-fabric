package me.marin.lockoutbutbetter.lockout.goals.obtain;

import me.marin.lockoutbutbetter.lockout.interfaces.ObtainItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class ObtainIronTools extends ObtainItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.IRON_AXE, Items.IRON_HOE, Items.IRON_PICKAXE, Items.IRON_SWORD, Items.IRON_SHOVEL);

    public ObtainIronTools(String id) {
        super(id);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain Iron Tools";
    }

}
