package me.marin.lockoutbutbetter.lockout;

import net.minecraft.item.ItemStack;

public abstract class Goal {

    private final String id;
    private boolean isCompleted = false;

    public Goal(String id) {
        this.id = id;
    }

    public abstract String getGoalName();
    public abstract ItemStack getTextureItemStack();

    public void setCompleted(boolean isCompleted) {
        this.isCompleted = isCompleted;
    }
    public boolean isCompleted() {
        return isCompleted;
    }
    public String getId() {
        return id;
    }

}
