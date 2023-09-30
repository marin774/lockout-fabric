package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.Tameable;

public abstract class TameAnimalGoal extends Goal {

    public TameAnimalGoal(String id, String data) {
        super(id, data);
    }

    public abstract EntityType<?> getAnimal();

}
