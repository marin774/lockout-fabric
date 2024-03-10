package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedUniqueAnimalsGoal;

public class Breed8UniqueAnimalsGoal extends BreedUniqueAnimalsGoal {

    public Breed8UniqueAnimalsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public int getAmount() {
        return 8;
    }

}
