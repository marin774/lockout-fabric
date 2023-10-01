package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public abstract class ConsumeItemGoal extends Goal {

    public ConsumeItemGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return getItem().getDefaultStack();
    }

    public abstract Item getItem();

}
