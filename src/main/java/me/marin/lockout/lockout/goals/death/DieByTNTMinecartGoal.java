package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class DieByTNTMinecartGoal extends Goal {

    public DieByTNTMinecartGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to TNT Minecart";
    }

    private static final ItemStack ITEM_STACK = Items.TNT_MINECART.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
