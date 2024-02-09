package me.marin.lockout.client.gui;

import me.marin.lockout.Utility;
import me.marin.lockout.client.LockoutClient;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

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
        this.renderBackground(context);
        Utility.drawCenterBingoBoard(context, mouseX, mouseY);
        Goal goal = Utility.getCenterHoveredGoal(context, mouseX, mouseY);
        if (goal != null) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
            List<OrderedText> lore = new ArrayList<>();
            lore.add(Text.of(((goal instanceof HasTooltipInfo) ? Formatting.UNDERLINE : "") + goal.getGoalName()).asOrderedText());
            if (goal instanceof HasTooltipInfo) {
                String s = LockoutClient.goalLoreMap.get(goal.getId());
                if (s != null) {
                    for (String t : s.split("\n")) {
                        lore.add(Text.of(t).asOrderedText());
                    }
                }
            }
            context.drawOrderedTooltip(textRenderer, lore, mouseX, mouseY);
        }
    }

    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {

    }

}
