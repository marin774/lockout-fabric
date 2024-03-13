package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

public abstract class DieToEntityGoal extends Goal {

    public DieToEntityGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    public abstract EntityType getEntityType();

}
