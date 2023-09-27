package me.marin.lockoutbutbetter;

import me.marin.lockoutbutbetter.client.LockoutButBetterClient;
import me.marin.lockoutbutbetter.lockout.Goal;
import me.marin.lockoutbutbetter.lockout.texture.CustomTextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.Generic3x3ContainerScreen;

import static me.marin.lockoutbutbetter.Constants.*;
import static me.marin.lockoutbutbetter.Constants.GUI_ITEM_SLOT_SIZE;

public class Utility {

    public static void drawBingoBoard(DrawContext context, int x, int y) {
        context.drawTexture(Constants.GUI_IDENTIFIER, x, y, 0, 0, GUI_WIDTH, GUI_HEIGHT, GUI_WIDTH, GUI_HEIGHT);

        x += GUI_FIRST_ITEM_OFFSET;
        y += GUI_FIRST_ITEM_OFFSET;
        final int startX = x;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Goal goal = Lockout.getInstance().getBoard().getGoals().get(j + 5 * i);
                if (goal != null) {
                    if (goal.isCompleted()) {
                        context.fill(x, y, x + 16, y + 16, 0xFF00BBAA);
                    }

                    if (goal instanceof CustomTextureProvider customTextureProvider) {
                        customTextureProvider.renderTexture(context, x, y, LockoutButBetterClient.CURRENT_TICK);
                    } else {
                        context.drawItem(goal.getTextureItemStack(), x, y);
                    }
                }
                x += GUI_ITEM_SLOT_SIZE;
            }
            y += GUI_ITEM_SLOT_SIZE;
            x = startX;
        }
    }

    public static void drawCenterBingoBoard(DrawContext context, int mouseX, int mouseY) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        int x = width / 2 - Constants.GUI_WIDTH / 2;
        int y = height / 2 - Constants.GUI_HEIGHT / 2;

        context.drawTexture(GUI_CENTER_IDENTIFIER, x, y, 0, 0, GUI_CENTER_WIDTH, GUI_CENTER_HEIGHT, GUI_CENTER_WIDTH, GUI_CENTER_HEIGHT);

        x += GUI_CENTER_FIRST_ITEM_OFFSET_X;
        y += GUI_CENTER_FIRST_ITEM_OFFSET_Y;
        final int startX = x;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                Goal goal = Lockout.getInstance().getBoard().getGoals().get(j + 5 * i);
                if (goal != null) {
                    if (goal.isCompleted()) {
                        context.fill(x, y, x + 16, y + 16, 0xFF00BBAA);
                    }

                    if (goal instanceof CustomTextureProvider customTextureProvider) {
                        customTextureProvider.renderTexture(context, x, y, LockoutButBetterClient.CURRENT_TICK);
                    } else {
                        context.drawItem(goal.getTextureItemStack(), x, y);
                    }
                    if (goal == getCenterHoveredGoal(context, mouseX, mouseY)) {
                        context.fill(x, y, x + 16, y + 16, 400, -2130706433);
                    }
                }
                x += GUI_CENTER_ITEM_SLOT_SIZE;
            }
            y += GUI_CENTER_ITEM_SLOT_SIZE;
            x = startX;
        }
    }

    public static Goal getCenterHoveredGoal(DrawContext context, int mouseX, int mouseY) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        int x = width / 2 - Constants.GUI_WIDTH / 2 + GUI_CENTER_FIRST_ITEM_OFFSET_X;
        int y = height / 2 - Constants.GUI_HEIGHT / 2 + GUI_CENTER_FIRST_ITEM_OFFSET_Y;
        final int startX = x;

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (mouseX >= x-1 && mouseX < x+18 && mouseY >= y-1 && mouseY < y+18) {
                    return Lockout.getInstance().getBoard().getGoals().get(j + i * 5);
                }
                x += GUI_CENTER_ITEM_SLOT_SIZE;
            }
            y += GUI_CENTER_ITEM_SLOT_SIZE;
            x = startX;
        }

        return null;
    }

}
