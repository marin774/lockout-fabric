package me.marin.lockoutbutbetter.client.gui;

import com.mojang.datafixers.kinds.Const;
import me.marin.lockoutbutbetter.Constants;
import me.marin.lockoutbutbetter.Utility;
import me.marin.lockoutbutbetter.lockout.Goal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;

public class BoardScreen extends HandledScreen<BoardScreenHandler> {

    public BoardScreen(BoardScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        Utility.drawCenterBingoBoard(context, mouseX, mouseY);
        Goal goal = Utility.getCenterHoveredGoal(context, mouseX, mouseY);
        if (goal != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            context.drawTooltip(textRenderer, Text.literal(goal.getGoalName()), mouseX, mouseY);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // TODO: render tooltip
    }



}
