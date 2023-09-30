package me.marin.lockout.lockout.goals.consume.unique;

import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;

public class Eat15UniqueFoodsGoal extends EatUniqueFoodsGoal {

    public Eat15UniqueFoodsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat 15 Unique Food";
    }

    @Override
    public int getAmount() {
        return 15;
    }
}
