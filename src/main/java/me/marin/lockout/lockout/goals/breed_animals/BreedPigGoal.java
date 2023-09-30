package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BreedPigGoal extends BreedAnimalGoal {

    public BreedPigGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Pigs";
    }

    @Override
    public ItemStack getTextureItemStack() {
        ItemStack item = Items.CARROT.getDefaultStack();
        item.setCount(2);
        return item;
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.PIG;
    }

}
