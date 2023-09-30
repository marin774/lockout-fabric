package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BreedChickenGoal extends BreedAnimalGoal {

    public BreedChickenGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Chicken";
    }

    @Override
    public ItemStack getTextureItemStack() {
        ItemStack item = Items.WHEAT_SEEDS.getDefaultStack();
        item.setCount(2);
        return item;
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.CHICKEN;
    }

}
