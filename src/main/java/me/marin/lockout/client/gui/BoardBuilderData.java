package me.marin.lockout.client.gui;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static me.marin.lockout.Constants.MAX_BOARD_SIZE;
import static me.marin.lockout.Constants.MIN_BOARD_SIZE;

public class BoardBuilderData {

    public static final BoardBuilderData INSTANCE;
    static {
        INSTANCE = new BoardBuilderData();
    }

    private int size = 5;
    private final List<Goal> goals = new ArrayList<>();
    private String title = "";
    private Integer editingIdx = null;
    private String search = "";

    private BoardBuilderData() {
        for (int i = 0; i < size * size; i++) {
            goals.add(null);
        }
    }

    public void clear() {
        for (int i = 0; i < size * size; i++) {
            goals.set(i, null);
        }
        editingIdx = null;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getEditingIdx() {
        return editingIdx;
    }

    public Goal getEditingGoal() {
        return goals.get(editingIdx);
    }

    public void setEditingIdx(Integer editingIdx) {
        this.editingIdx = editingIdx;
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public int size() {
        return size;
    }

    public boolean incrementSize() {
        if (size == MAX_BOARD_SIZE)
            throw new IllegalStateException("Cannot increment at maximum size");
        goals.addAll(Collections.nCopies(2 * size + 1, null));
        size = Math.min(size + 1, MAX_BOARD_SIZE);
        return size == MAX_BOARD_SIZE;
    }

    public boolean decrementSize() {
        if (size == MIN_BOARD_SIZE)
            throw new IllegalStateException("Cannot decrement at minimum size");
        goals.subList(goals.size() - 2 * size + 1, goals.size()).clear();
        size = Math.max(size - 1, MIN_BOARD_SIZE);
        if (editingIdx != null && editingIdx > goals.size())
            editingIdx = goals.size();
        return size == MIN_BOARD_SIZE;
    }

    public List<Goal> getGoals() {
        return Collections.unmodifiableList(goals);
    }

    public void setGoal(Goal goal) {
        goals.set(editingIdx, goal);
    }

}
