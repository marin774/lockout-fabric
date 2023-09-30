package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ReachBedrockGoal extends Goal {

    public ReachBedrockGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Reach Bedrock";
    }

    private static ItemStack ITEM_STACK = Items.BEDROCK.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}