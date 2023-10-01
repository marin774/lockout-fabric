package me.marin.lockout.lockout;

import me.marin.lockout.LockoutTeam;
import net.minecraft.item.ItemStack;

public abstract class Goal {

    private final String id;
    @SuppressWarnings("unused")
    private final String data;
    private boolean isCompleted = false;
    private LockoutTeam completedTeam = null;

    public Goal(String id, String data) {
        this.id = id;
        this.data = data;
    }

    public abstract String getGoalName();

    /**
     * Displays this ItemStack on the board.
     * Also used as a fallback if CustomTextureRenderer fails to render (returns false).
     */
    public abstract ItemStack getTextureItemStack();

    public void setCompleted(boolean isCompleted, LockoutTeam team) {
        this.isCompleted = isCompleted;
        this.completedTeam = team;
    }
    public boolean isCompleted() {
        return isCompleted;
    }

    public LockoutTeam getCompletedTeam() {
        return completedTeam;
    }

    public String getId() {
        return id;
    }

    public String getData() {
        return data;
    }
}
