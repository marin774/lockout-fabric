package me.marin.lockout.lockout.goals.consume.unique;

import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;

public class Eat5UniqueFoodsGoal extends EatUniqueFoodsGoal {

    public Eat5UniqueFoodsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat 5 Unique Food";
    }

    @Override
    public int getAmount() {
        return 5;
    }
}
