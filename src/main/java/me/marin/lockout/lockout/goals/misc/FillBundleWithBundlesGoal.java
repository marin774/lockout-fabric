package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class FillBundleWithBundlesGoal extends Goal {

    private static final ItemStack ITEM = Items.BUNDLE.getDefaultStack();
    static {
        ITEM.setCount(16);
    }

    public FillBundleWithBundlesGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Fill Bundle with 16 empty Bundles";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM;
    }
}
