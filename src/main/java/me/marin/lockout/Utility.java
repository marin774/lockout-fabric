package me.marin.lockout;

import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.client.LockoutClient;
import me.marin.lockout.client.gui.BoardBuilderScreen;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static me.marin.lockout.Constants.*;
import static me.marin.lockout.LockoutConfig.BoardSide.LEFT;

public class Utility {

    public static void drawBingoBoard(DrawContext context) {
        LockoutConfig.BoardSide boardSide = LockoutConfig.getInstance().boardSide;

        // Don't render board if F3 is open with left-side board.
        if (boardSide == LockoutConfig.BoardSide.LEFT && MinecraftClient.getInstance().inGameHud.getDebugHud().shouldShowDebugHud()) {
            return;
        }

        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        Lockout lockout = LockoutClient.lockout;
        LockoutBoard board = lockout.getBoard();

        int boardWidth = 2 * GUI_PADDING + board.size() * GUI_SLOT_SIZE;
        int boardHeight = GUI_PADDING + GUI_PADDING_BOTTOM + board.size() * GUI_SLOT_SIZE;

        int boardRightEdgeX = boardSide == LEFT ? boardWidth : context.getScaledWindowWidth();
        int boardLeftEdgeX = boardRightEdgeX - boardWidth;

        int x = boardLeftEdgeX;
        int y = 0;

        context.drawGuiTexture(RenderLayer::getGuiTextured, Constants.GUI_IDENTIFIER, x, y, boardWidth, boardHeight);

        x += GUI_PADDING + 1;
        y += GUI_PADDING + 1;
        final int startX = x;

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                Goal goal = board.getGoals().get(j + board.size() * i);
                if (goal != null) {
                    if (goal.isCompleted()) {
                        context.fill(x, y, x + 16, y + 16, (0xFF << 24) | goal.getCompletedTeam().getColor().getColorValue());
                    }

                    goal.render(context, textRenderer, x, y);

                }
                x += GUI_SLOT_SIZE;
            }
            y += GUI_SLOT_SIZE;
            x = startX;
        }
        x += 2;
        y += 1;
        List<String> pointsList = new ArrayList<>();
        for (LockoutTeam team : lockout.getTeams()) {
            pointsList.add(team.getColor() + "" + team.getPoints() + Formatting.RESET);
        }

        context.drawText(textRenderer, String.join(Formatting.RESET + "" + Formatting.GRAY + "-", pointsList), x, y, 0, true);

        String timer = Utility.ticksToTimer(lockout.getTicks());
        context.drawText(textRenderer, Formatting.WHITE + timer, boardRightEdgeX - textRenderer.getWidth(timer) - 4, y, 0, true);

        List<String> formattedNames = new ArrayList<>();
        int maxWidth = 0;
        for (LockoutTeam team : lockout.getTeams()) {
            for (String playerName : team.getPlayerNames()) {
                formattedNames.add(team.getColor() + playerName);
                maxWidth = Math.max(maxWidth, textRenderer.getWidth(playerName));
            }
        }

        y += 20;
        switch (boardSide) {
            case RIGHT -> {
                context.fill(context.getScaledWindowWidth() - maxWidth - 3 - 1,  y - 2, context.getScaledWindowWidth() - 1, y + formattedNames.size() * textRenderer.fontHeight + 1, 0x80_00_00_00);

                for (String formattedName : formattedNames) {
                    context.drawText(textRenderer, formattedName, context.getScaledWindowWidth() - textRenderer.getWidth(formattedName) - 2, y, 0, true);
                    y += textRenderer.fontHeight;
                }
            }
            case LEFT -> {
                context.fill(1,  y - 2, 4 + maxWidth, y + formattedNames.size() * textRenderer.fontHeight + 1, 0x80_00_00_00);

                for (String formattedName : formattedNames) {
                    context.drawText(textRenderer, formattedName, 3, y, 0, true);
                    y += textRenderer.fontHeight;
                }
            }
        }

    }

    public static void drawCenterBingoBoard(DrawContext context, TextRenderer textRenderer, int mouseX, int mouseY) {
        int width = context.getScaledWindowWidth();
        int height = context.getScaledWindowHeight();

        LockoutBoard board = LockoutClient.lockout.getBoard();

        int boardWidth = 2 * GUI_CENTER_PADDING + board.size() * GUI_CENTER_SLOT_SIZE;
        int x = width / 2 - boardWidth / 2;

        int boardHeight = 2 * GUI_CENTER_PADDING + board.size() * GUI_CENTER_SLOT_SIZE;
        int y = height / 2 - boardHeight / 2;

        context.drawGuiTexture(RenderLayer::getGuiTextured, GUI_CENTER_IDENTIFIER, x, y, boardWidth, boardHeight);

        x += GUI_CENTER_PADDING + 1;
        y += GUI_CENTER_PADDING + 1;
        final int startX = x;

        Goal hoveredGoal = getBoardHoveredGoal(context, mouseX, mouseY);

        for (int i = 0; i < board.size(); i++) {
            for (int j = 0; j < board.size(); j++) {
                Goal goal = board.getGoals().get(j + board.size() * i);
                if (goal != null) {
                    if (goal.isCompleted()) {
                        context.fill(x, y, x + 16, y + 16, (0xFF << 24) | goal.getCompletedTeam().getColor().getColorValue());
                    }

                    goal.render(context, textRenderer, x, y);

                    if (goal == hoveredGoal) {
                        context.fill(x, y, x + 16, y + 16, 400, GUI_CENTER_HOVERED_COLOR);
                    }
                }
                x += GUI_CENTER_SLOT_SIZE;
            }
            y += GUI_CENTER_SLOT_SIZE;
            x = startX;
        }
    }

    public static Optional<Integer> getBoardHoveredIndex(int size, int width, int height, int mouseX, int mouseY) {
        int x = width / 2 - (2 * GUI_CENTER_PADDING + size * GUI_CENTER_SLOT_SIZE) / 2 + GUI_CENTER_PADDING - BoardBuilderScreen.CENTER_OFFSET;
        int y = height / 2 - (2 * GUI_CENTER_PADDING + size * GUI_CENTER_SLOT_SIZE) / 2 + GUI_CENTER_PADDING;
        final int startX = x;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (mouseX >= x-1 && mouseX < x + GUI_CENTER_SLOT_SIZE && mouseY >= y-1 && mouseY < y + GUI_CENTER_SLOT_SIZE) {
                    return Optional.of(j + i * size);
                }
                x += GUI_CENTER_SLOT_SIZE;
            }
            y += GUI_CENTER_SLOT_SIZE;
            x = startX;
        }

        return Optional.empty();
    }

    public static Goal getBoardHoveredGoal(DrawContext context, int mouseX, int mouseY) {
        Optional<Integer> hoveredIdx = getBoardHoveredIndex(LockoutClient.lockout.getBoard().size(), context.getScaledWindowWidth(), context.getScaledWindowHeight(), mouseX, mouseY);
        return hoveredIdx.map(integer -> LockoutClient.lockout.getBoard().getGoals().get(integer)).orElse(null);
    }

    public static void drawGoalInformation(DrawContext context, TextRenderer textRenderer, Goal goal, int mouseX, int mouseY) {
        List<OrderedText> tooltip = new ArrayList<>();
        tooltip.add(Text.of(((goal instanceof HasTooltipInfo) ? Formatting.UNDERLINE : "") + goal.getGoalName()).asOrderedText());
        if (goal instanceof HasTooltipInfo) {
            String s = LockoutClient.goalTooltipMap.get(goal.getId());
            if (s != null) {
                for (String t : s.split("\n")) {
                    tooltip.add(Text.of(t).asOrderedText());
                }
            }
        }
        context.drawOrderedTooltip(textRenderer, tooltip, mouseX, mouseY);
    }

    /**
     * Code from {@link DrawContext#drawStackCount(TextRenderer, ItemStack, int, int, String)}, but without ItemStack argument requirement
     */
    public static void drawStackCount(DrawContext context, int x, int y, String count) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;
        context.getMatrices().push();
        context.getMatrices().translate(0.0F, 0.0F, 200.0F);
        context.drawText(textRenderer, count, x + 19 - 2 - textRenderer.getWidth(count), y + 6 + 3, -1, true);
        context.getMatrices().pop();
    }

    public static List<ServerPlayerEntity> getSpectators(Lockout lockout, MinecraftServer server) {
        return server.getPlayerManager().getPlayerList()
                .stream()
                .filter(p -> !lockout.isLockoutPlayer(p.getUuid()))
                .toList();
    }

    public static String ticksToTimer(long ticks) {
        ticks = Math.abs(ticks);
        long second = (ticks / 20) % 60;
        long minute = ((ticks / 20) / 60) % 60;
        long hour = ((ticks / 20) / 60 / 60) % 24;

        String time;
        if (hour > 0) {
            time = String.format("%02d:%02d:%02d", hour, minute, second);
        } else {
            time = String.format("%02d:%02d", minute, second);
        }

        return time;
    }

}
