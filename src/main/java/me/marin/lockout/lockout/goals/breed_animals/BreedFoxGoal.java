package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BreedFoxGoal extends BreedAnimalGoal {

    public BreedFoxGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Fox";
    }

    @Override
    public ItemStack getTextureItemStack() {
        ItemStack item = Items.SWEET_BERRIES.getDefaultStack();
        item.setCount(2);
        return item;
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.FOX;
    }

}
