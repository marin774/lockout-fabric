package me.marin.lockout.client.gui;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import me.marin.lockout.lockout.Goal;

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
     * @return true if maximum size has been reached, false otherwise
     */
    public boolean incrementSize() {
        if (size == MAX_BOARD_SIZE) {
            throw new IllegalStateException("Cannot increment at maximum size");
        }
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
        if (modifyingIdx != null && modifyingIdx > goals.size()) {
            modifyingIdx = goals.size(); // TODO: set this to null (requires more work)
        }
        return size == MIN_BOARD_SIZE;
    }

    /**
     * Returns an unmodifiable view of the goals list.
     */
    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    public void setGoal(Goal goal) {
        goals.set(modifyingIdx, goal);
    }

}
