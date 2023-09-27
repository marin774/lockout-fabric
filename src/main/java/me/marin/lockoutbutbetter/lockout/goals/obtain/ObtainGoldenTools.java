package me.marin.lockoutbutbetter.lockout.goals.obtain;

import me.marin.lockoutbutbetter.lockout.interfaces.ObtainItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class ObtainGoldenTools extends ObtainItemsGoal {

    private static final List<Item> ITEMS = List.of(Items.GOLDEN_AXE, Items.GOLDEN_HOE, Items.GOLDEN_PICKAXE, Items.GOLDEN_SWORD, Items.GOLDEN_SHOVEL);

    public ObtainGoldenTools(String id) {
        super(id);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain Golden Tools";
    }

}
