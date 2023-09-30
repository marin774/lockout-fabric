package me.marin.lockout.lockout.goals.status_effect;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RemoveStatusEffectUsingMilkGoal extends Goal {

    private static final ItemStack ITEM_STACK = Items.MILK_BUCKET.getDefaultStack();

    public RemoveStatusEffectUsingMilkGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Remove an effect using Milk Bucket";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
