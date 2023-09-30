package me.marin.lockout.lockout.goals.tame_animal;

import me.marin.lockout.lockout.interfaces.TameAnimalGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TameParrotGoal extends TameAnimalGoal {

    public TameParrotGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Tame a Parrot";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return Items.PARROT_SPAWN_EGG.getDefaultStack(); //TODO
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.PARROT;
    }
}
