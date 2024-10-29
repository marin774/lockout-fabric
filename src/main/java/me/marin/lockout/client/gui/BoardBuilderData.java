package me.marin.lockout.client.gui;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.marin.lockout.Constants.MAX_BOARD_SIZE;
import static me.marin.lockout.Constants.MIN_BOARD_SIZE;

public class BoardBuilderData {

    public static final BoardBuilderData INSTANCE = new BoardBuilderData();

    private int size = 5;
    private final List<Goal> goals = new ArrayList<>();
    private String title = "";
    private Integer editingIdx = null;
    private String search = "";

    private BoardBuilderData() {
        goals = new ArrayList<>(Collections.nCopies(size * size, null));
    }

    public void clear() {
        Collections.fill(goals, null);
        modifyingIdx = null;
    }

    public Goal getEditingGoal() {
        return goals.get(editingIdx);
    }

    /**
     * Increases the board size by 1 by adding a column to the right and a row to the bottom of the board.
     * @return true if maximum size has been reached, false otherwise
     */
    public boolean incrementSize() {
        if (size == MAX_BOARD_SIZE)
            throw new IllegalStateException("Cannot increment at maximum size");
        goals.addAll(Collections.nCopies(2 * size + 1, null));
        size = Math.min(size + 1, MAX_BOARD_SIZE);
        return size == MAX_BOARD_SIZE;
    }

    /**
     * Decreases the board size by 1 by removing the rightmost column and the bottommost row.
     * Any goals in the removed slots are voided.
     *
     * @return true if minimum size has been reached, false otherwise
     */
    public boolean decrementSize() {
        if (size == MIN_BOARD_SIZE) {
            throw new IllegalStateException("Cannot decrement at minimum size");
        }
        goals.subList(goals.size() - 2 * size + 1, goals.size()).clear();
        size = Math.max(size - 1, MIN_BOARD_SIZE);
        if (editingIdx != null && editingIdx > goals.size())
            editingIdx = goals.size();
        return size == MIN_BOARD_SIZE;
    }

    /**
     * Returns an unmodifiable view of the goals list.
     */
    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    public void setGoal(Goal goal) {
        goals.set(editingIdx, goal);
    }

}
