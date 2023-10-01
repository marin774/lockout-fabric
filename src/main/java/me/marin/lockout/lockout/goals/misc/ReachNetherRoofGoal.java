package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ReachNetherRoofGoal extends Goal {

    public ReachNetherRoofGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Get to Nether Roof";
    }

    private static final ItemStack ITEM_STACK = Items.BEDROCK.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}