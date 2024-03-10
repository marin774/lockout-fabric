package me.marin.lockout.lockout.goals.breed_animals;

import me.marin.lockout.lockout.interfaces.BreedUniqueAnimalsGoal;

public class Breed6UniqueAnimalsGoal extends BreedUniqueAnimalsGoal {

    public Breed6UniqueAnimalsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public int getAmount() {
        return 6;
    }

}
