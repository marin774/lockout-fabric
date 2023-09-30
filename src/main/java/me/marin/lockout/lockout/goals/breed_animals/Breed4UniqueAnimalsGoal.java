package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedUniqueAnimalsGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Breed4UniqueAnimalsGoal extends BreedUniqueAnimalsGoal {

    public Breed4UniqueAnimalsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Breed 4 Unique Animals";
    }

    @Override
    public ItemStack getTextureItemStack() {
        ItemStack item = Items.WHEAT.getDefaultStack();
        item.setCount(getAmount());
        return item;
    }

    @Override
    public int getAmount() {
        return 4;
    }

}
