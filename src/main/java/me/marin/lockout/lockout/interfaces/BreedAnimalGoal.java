package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;

public abstract class BreedAnimalGoal extends Goal {

    public BreedAnimalGoal(String id, String data) {
        super(id, data);
    }

    public abstract EntityType<?> getAnimal();

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

}
