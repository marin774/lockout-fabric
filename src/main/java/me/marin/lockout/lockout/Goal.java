package me.marin.lockout.lockout;

import lombok.Getter;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.client.LockoutClient;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;

import java.util.Objects;

public abstract class Goal {

    @Getter
    private final String id;
    @Getter
    private final String data;
    private boolean isCompleted = false;
    @Getter
    private LockoutTeam completedTeam;

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

    public final void render(DrawContext context, TextRenderer textRenderer, int x, int y) {
        boolean success = false;
        if (this instanceof CustomTextureRenderer customTextureRenderer) {
            success = customTextureRenderer.renderTexture(context, x, y, LockoutClient.CURRENT_TICK);
        }
        if (!success) {
            // TODO: handle null
            context.drawItem(this.getTextureItemStack(), x, y);
            context.drawStackOverlay(textRenderer, this.getTextureItemStack(), x, y);
        }
    }

    public boolean hasData() {
        return data != null && !Objects.equals(data, GoalDataConstants.DATA_NONE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Goal goal = (Goal) o;
        return id.equals(goal.id) && Objects.equals(data, goal.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, data);
    }

}
