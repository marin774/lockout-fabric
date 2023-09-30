package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;

public abstract class BreedUniqueAnimalsGoal extends Goal implements RequiresAmount {

    public BreedUniqueAnimalsGoal(String id, String data) {
        super(id, data);
    }

}
