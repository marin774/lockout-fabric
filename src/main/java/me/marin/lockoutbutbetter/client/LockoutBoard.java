package me.marin.lockoutbutbetter.client;

import me.marin.lockoutbutbetter.lockout.Goal;
import me.marin.lockoutbutbetter.lockout.GoalRegistry;
import me.marin.lockoutbutbetter.lockout.GoalType;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LockoutBoard {

    private final List<Goal> goals = new ArrayList<>();

    public LockoutBoard(List<String> goals) {
        for (String id : goals) {
            if (GoalType.NULL.equals(id)) {
                this.goals.add(null);
                continue;
            }
            this.goals.add(GoalRegistry.INSTANCE.newGoal(id));
        }
    }

    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

}
