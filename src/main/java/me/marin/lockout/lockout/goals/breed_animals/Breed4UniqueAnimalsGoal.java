package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedUniqueAnimalsGoal;

public class Breed4UniqueAnimalsGoal extends BreedUniqueAnimalsGoal {

    public Breed4UniqueAnimalsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public int getAmount() {
        return 4;
    }

}
