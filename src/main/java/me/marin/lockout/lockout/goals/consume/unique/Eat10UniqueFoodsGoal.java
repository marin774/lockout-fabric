package me.marin.lockout.lockout.goals.consume.unique;

import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;

public class Eat10UniqueFoodsGoal extends EatUniqueFoodsGoal {

    public Eat10UniqueFoodsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat 10 Unique Food";
    }

    @Override
    public int getAmount() {
        return 10;
    }

}
