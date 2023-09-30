package me.marin.lockout.lockout.goals.consume.unique;

import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;

public class Eat25UniqueFoodsGoal extends EatUniqueFoodsGoal {

    public Eat25UniqueFoodsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat 25 Unique Food";
    }

    @Override
    public int getAmount() {
        return 25;
    }
}
