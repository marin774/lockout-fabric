package me.marin.lockout.client;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.GoalType;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LockoutBoard {

    private final List<Goal> goals = new ArrayList<>();

    public LockoutBoard(List<Pair<String, String>> goals) {
        for (int i = 0; i < 25; i++) {
            Pair<String, String> goal = goals.get(i);
            if (GoalType.NULL.equals(goal.getA())) {
                this.goals.add(null);
                continue;
            }
            this.goals.add(GoalRegistry.INSTANCE.newGoal(goal.getA(), goal.getB()));
        }
    }

    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

}
