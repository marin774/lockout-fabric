package me.marin.lockout.client;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.GoalType;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.marin.lockout.Constants.MAX_BOARD_SIZE;
import static me.marin.lockout.Constants.MIN_BOARD_SIZE;

public class LockoutBoard {

    private final int size;

    private final List<Goal> goals = new ArrayList<>();

    public LockoutBoard(List<Pair<String, String>> goals) {
        size = (int) Math.sqrt(goals.size());
        if (goals.size() != size * size || size < MIN_BOARD_SIZE || size > MAX_BOARD_SIZE) {
            throw new IllegalArgumentException(String.format("Invalid number of goals (%d)", size));
        }
        for (Pair<String, String> goal : goals) {
            if (GoalType.NULL.equals(goal.getA())) {
                this.goals.add(null);
                continue;
            }
            this.goals.add(GoalRegistry.INSTANCE.newGoal(goal.getA(), goal.getB()));
        }
    }

    public int size() {
        return size;
    }

    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

}
