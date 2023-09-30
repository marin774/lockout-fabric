package me.marin.lockout.lockout.goals.tame_animal;

import me.marin.lockout.lockout.interfaces.TameAnimalGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class TameHorseGoal extends TameAnimalGoal {

    public TameHorseGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Tame a Horse";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return Items.HORSE_SPAWN_EGG.getDefaultStack(); //TODO
    }

    @Override
    public EntityType<?> getAnimal() {
        return EntityType.HORSE;
    }
}
