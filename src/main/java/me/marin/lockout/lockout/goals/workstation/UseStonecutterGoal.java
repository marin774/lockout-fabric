package me.marin.lockout.lockout.goals.workstation;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class UseStonecutterGoal extends Goal {

    public UseStonecutterGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Use Stonecutter";
    }

    private static final ItemStack ITEM_STACK = Items.STONECUTTER.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
