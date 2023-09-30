package me.marin.lockout.lockout.goals.consume.unique;

import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;

public class Eat20UniqueFoodsGoal extends EatUniqueFoodsGoal {

    public Eat20UniqueFoodsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat 20 Unique Food";
    }

    @Override
    public int getAmount() {
        return 20;
    }
}
