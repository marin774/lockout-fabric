package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BreedStriderGoal extends BreedAnimalGoal {

    public BreedStriderGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed Striders";
    }

    @Override
    public ItemStack getTextureItemStack() {
        ItemStack item = Items.CRIMSON_FUNGUS.getDefaultStack();
        item.setCount(2);
        return item;
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.STRIDER;
    }

}
