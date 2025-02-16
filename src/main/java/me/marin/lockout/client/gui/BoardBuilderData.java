package me.marin.lockout.client.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.marin.lockout.Constants.MAX_BOARD_SIZE;
import static me.marin.lockout.Constants.MIN_BOARD_SIZE;

/**
 * Stores information about BoardBuilderScreen independently of the GUI.
 * All important states are saved here (including filled goals, search query in the search bar etc.)
 */
public class BoardBuilderData {

    public static final BoardBuilderData INSTANCE = new BoardBuilderData();

    private final List<Goal> goals;

    @Getter @Setter
    private String title = "";

    /**
     * Index of the goal that is currently being modified (searching goal or editing data)
     */
    @Setter @Getter
    private Integer modifyingIdx = null;

    @Setter @Getter
    private String search = "";

    @Accessors(fluent = true) // size() instead of getSize()
    @Getter
    private int size = 5;

    private BoardBuilderData() {
        goals = new ArrayList<>(Collections.nCopies(size * size, null));
    }

    public void clear() {
        Collections.fill(goals, null);
        modifyingIdx = null;
    }

    public Goal getModifyingGoal() {
        return goals.get(modifyingIdx);
    }

    /**
     * Increases the board size by 1 by adding a column to the right and a row to the bottom of the board.
     */
    public void incrementSize() {
        if (size == MAX_BOARD_SIZE) {
            throw new IllegalStateException("Cannot increment at maximum size");
        }
        int modifyingRow = modifyingIdx == null ? 0 : modifyingIdx / size;
        int modifyingColumn = modifyingIdx == null ? 0 : modifyingIdx % size;

        size += 1;

        // add column to the right (without bottom right corner)
        for (int i = 0; i < size - 1; i++) {
            goals.add((size * i) + (size - 1), null);
        }

        if (modifyingIdx != null) {
            modifyingIdx = modifyingRow * size + modifyingColumn;
        }

        // add row to the bottom (including bottom right corner)
        goals.addAll(Collections.nCopies(size, null));
    }

    /**
     * Decreases the board size by 1 by removing the rightmost column and the bottommost row.
     * Any goals in the removed slots are voided.
     */
    public void decrementSize() {
        if (size == MIN_BOARD_SIZE) {
            throw new IllegalStateException("Cannot decrement at minimum size");
        }
        int modifyingRow = modifyingIdx == null ? 0 : modifyingIdx / size;
        int modifyingColumn = modifyingIdx == null ? 0 : modifyingIdx % size;

        size -= 1;

        // remove the bottommost row
        for (int i = 0; i < size + 1; i++) {
            goals.removeLast();
        }

        // remove the rightmost column
        for (int i = size - 1; i >= 0; i--) {
            goals.remove((size + 1) * i + size);
        }

        if (modifyingIdx != null) {
            if (modifyingRow >= size) {
                modifyingRow -= 1;
            }
            if (modifyingColumn >= size) {
                modifyingColumn -= 1;
            }
            modifyingIdx = modifyingRow * size + modifyingColumn;
        }
    }

    /**
     * @return unmodifiable view of goals list
     */
    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    public void setGoal(Goal goal) {
        goals.set(modifyingIdx, goal);
    }

    public void setBoard(String title, int size, List<Pair<String, String>> goals) {
        this.title = title;
        this.size = size;
        this.modifyingIdx = null;

        this.goals.clear();

        for (Pair<String, String> pair : goals) {
            Goal goal = null;
            if (GoalRegistry.INSTANCE.isGoalValid(pair.getA(), pair.getB())) {
                goal = GoalRegistry.INSTANCE.newGoal(pair.getA(), pair.getB());
            }
            this.goals.add(goal);
        }
    }

}
