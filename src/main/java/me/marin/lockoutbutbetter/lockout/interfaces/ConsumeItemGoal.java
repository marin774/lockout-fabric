package me.marin.lockoutbutbetter.lockout.interfaces;

import me.marin.lockoutbutbetter.lockout.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ConsumeItemGoal extends Goal {

    public ConsumeItemGoal(String id) {
        super(id);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return getItem().getDefaultStack();
    }

    public abstract Item getItem();

}
