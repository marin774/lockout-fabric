package me.marin.lockout.client.gui;

import me.marin.lockout.Utility;
import me.marin.lockout.lockout.Goal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class BoardScreen extends Screen {

    public BoardScreen(Text title) {
        super(title);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        Utility.drawCenterBingoBoard(context, textRenderer, mouseX, mouseY);
        Goal hoveredGoal = Utility.getBoardHoveredGoal(context, mouseX, mouseY);
        if (hoveredGoal != null) {
            Utility.drawGoalInformation(context, textRenderer, hoveredGoal, mouseX, mouseY);
        }
    }

}
