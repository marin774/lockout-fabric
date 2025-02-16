package me.marin.lockout.client.gui;

import me.marin.lockout.Lockout;
import me.marin.lockout.Utility;
import me.marin.lockout.client.LockoutClient;
import me.marin.lockout.generator.GoalDataGenerator;
import me.marin.lockout.json.JSONBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.texture.CustomTextureRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static me.marin.lockout.Constants.*;

public class BoardBuilderScreen extends Screen {

    public static int CENTER_OFFSET = 0;
    public static boolean displaySearch = false;
    public static boolean displayEditData = false;

    private TextFieldWidget titleTextField;
    private ButtonWidget saveButton;
    private TextWidget saveErrorTextWidget;
    private ButtonWidget clearBoardButton;
    private ButtonWidget closeButton;
    private ButtonWidget closeSearchButton;
    private ButtonWidget increaseSizeButton;
    private ButtonWidget decreaseSizeButton;
    private TextFieldWidget searchTextField;
    private BoardBuilderSearchWidget boardBuilderSearchWidget;
    private ButtonWidget saveDataButton;
    private ButtonWidget closeEditDataButton;
    private TextWidget editDataErrorTextWidget;


    public BoardBuilderScreen() {
        super(Text.empty());
    }

    @Override
    protected void init() {
        super.init();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int centerX = width / 2;
        int centerY = height / 2;

        int boardHalfSize = GUI_PADDING + (BoardBuilderData.INSTANCE.size() * GUI_SLOT_SIZE) / 2;

        titleTextField = new TextFieldWidget(textRenderer, centerX - 60 - CENTER_OFFSET, centerY - boardHalfSize - 18 - 8, 120, 18, Text.empty());
        titleTextField.setChangedListener(BoardBuilderData.INSTANCE::setTitle);
        titleTextField.setText(BoardBuilderData.INSTANCE.getTitle());
        this.addDrawableChild(titleTextField);

        final int BOTTOM_BUTTONS_Y = height - 30;

        saveButton = ButtonWidget.builder(Text.of("Save Board"), (b) -> {
            saveGoals(10, height - 45);
        }).width(85).position(10, BOTTOM_BUTTONS_Y).build();
        this.addDrawableChild(saveButton);

        closeButton = ButtonWidget.builder(Text.of("Close"), (b) -> {
            close();
        }).width(50).position(width - 50 - 10, BOTTOM_BUTTONS_Y).build();
        this.addDrawableChild(closeButton);

        clearBoardButton = ButtonWidget.builder(Text.of("Clear Board"), (b) -> {
            BoardBuilderData.INSTANCE.clear();
            closeEditData();
            closeSearch();
        }).width(85).position(closeButton.getX() - 85 - 10, BOTTOM_BUTTONS_Y).build();
        this.addDrawableChild(clearBoardButton);

        increaseSizeButton = ButtonWidget.builder(Text.literal("+"), b -> {
            BoardBuilderData.INSTANCE.incrementSize();
            clearAndInit();
        }).tooltip(Tooltip.of(Text.literal("Increase board size"))).width(20).position(centerX + boardHalfSize - CENTER_OFFSET + 8, centerY - 10).build();
        increaseSizeButton.active = BoardBuilderData.INSTANCE.size() != MAX_BOARD_SIZE;
        this.addDrawableChild(increaseSizeButton);

        decreaseSizeButton = ButtonWidget.builder(Text.literal("-"), b -> {
            BoardBuilderData.INSTANCE.decrementSize();
            clearAndInit();
        }).tooltip(Tooltip.of(Text.literal("Decrease board size"))).width(20).position(centerX - boardHalfSize - CENTER_OFFSET - 20 - 8, centerY - 10).build();
        decreaseSizeButton.active = BoardBuilderData.INSTANCE.size() != MIN_BOARD_SIZE;
        this.addDrawableChild(decreaseSizeButton);

        if (displaySearch) {
            double scrollY = boardBuilderSearchWidget == null ? 0 : boardBuilderSearchWidget.getScrollY();
            boardBuilderSearchWidget = new BoardBuilderSearchWidget(
                    centerX + boardHalfSize + 35 - CENTER_OFFSET,
                    40,
                    width / 2 - 125 + CENTER_OFFSET,
                    height - 40 * 2, Text.empty());
            boardBuilderSearchWidget.setScrollY(scrollY);
            this.addDrawableChild(boardBuilderSearchWidget);

            closeSearchButton = ButtonWidget.builder(Text.of("<"), (b) -> {
                closeSearch();
            }).tooltip(Tooltip.of(Text.of("Close search"))).width(20).position(boardBuilderSearchWidget.getX(), boardBuilderSearchWidget.getY() - 21).build();
            this.addDrawableChild(closeSearchButton);

            searchTextField = new TextFieldWidget(textRenderer, closeSearchButton.getX() + closeSearchButton.getWidth() + 1 + 5, closeSearchButton.getY() + 1, boardBuilderSearchWidget.getWidth() - closeSearchButton.getWidth() - 2 - 5, 18, Text.empty());
            searchTextField.setChangedListener(s -> {
                BoardBuilderData.INSTANCE.setSearch(s);
                boardBuilderSearchWidget.searchUpdated(s);
            });
            if (!BoardBuilderData.INSTANCE.getSearch().isEmpty()) {
                searchTextField.setText(BoardBuilderData.INSTANCE.getSearch());
            }
            this.addDrawableChild(searchTextField);
        }
        if (displayEditData) {
            Goal goal = BoardBuilderData.INSTANCE.getModifyingGoal();
            var generators = GoalRegistry.INSTANCE.getDataGenerator(goal.getId()).get().getGenerators();
            List<String> dataList = new ArrayList<>(List.of(goal.getData().split(GoalDataConstants.DATA_SEPARATOR)));
            int x = centerX + 100 - CENTER_OFFSET;
            int y = centerY - (18 + generators.size() * 45) / 2;

            for (int i = 0; i < generators.size(); i++) {
                final int idx = i;
                GoalDataGenerator.Generator<?> generator = generators.get(i);
                TextWidget textWidget = new TextWidget(Text.of(generator.getGeneratorName()), textRenderer);
                textWidget.setPosition(x, y);
                this.addDrawableChild(textWidget);

                y += 15;

                TextFieldWidget textFieldWidget = new TextFieldWidget(textRenderer, x, y, 150, 18, Text.empty());
                textFieldWidget.setText(dataList.get(idx));
                textFieldWidget.setChangedListener(s -> {
                    dataList.set(idx, s);
                });
                this.addDrawableChild(textFieldWidget);

                y += 30;
            }

            closeEditDataButton = ButtonWidget.builder(Text.of("<"), (b) -> {
                closeEditData();
            }).tooltip(Tooltip.of(Text.of("Close 'Edit Data'"))).width(20).position(x, y).build();
            this.addDrawableChild(closeEditDataButton);

            int errorY = y + 25;
            saveDataButton = ButtonWidget.builder(Text.of("Save"), (b) -> {
                StringBuilder sb = new StringBuilder();
                boolean isOk = true;
                String wrongDataGenerator = null;
                for (int i = 0; i < generators.size(); i++) {
                    if (i > 0) sb.append(GoalDataConstants.DATA_SEPARATOR);
                    GoalDataGenerator.Generator<?> generator = generators.get(i);
                    if (!generator.verify(dataList.get(i))) {
                        isOk = false;
                        wrongDataGenerator = generator.getGeneratorName();
                        break;
                    }
                    sb.append(dataList.get(i));
                }
                if (editDataErrorTextWidget != null) {
                    this.remove(editDataErrorTextWidget);
                    this.editDataErrorTextWidget = null;
                }
                if (!isOk) {
                    String s = "Invalid '" + wrongDataGenerator + "'.";
                    editDataErrorTextWidget = new TextWidget(Text.of(s), textRenderer);
                    editDataErrorTextWidget.setTextColor(Color.RED.getRGB());
                    editDataErrorTextWidget.setPosition(x + 75 - textRenderer.getWidth(s) / 2, errorY);
                    this.addDrawableChild(editDataErrorTextWidget);
                    return;
                }
                BoardBuilderData.INSTANCE.setGoal(GoalRegistry.INSTANCE.newGoal(goal.getId(), sb.toString()));
                closeEditData();
            }).width(50).position(closeEditDataButton.getX() + closeEditDataButton.getWidth() + 5, closeEditDataButton.getY()).build();
            this.addDrawableChild(saveDataButton);
        }

    }

    private void saveGoals(int errorX, int errorY) {
        List<Goal> goals = BoardBuilderData.INSTANCE.getGoals();
        JSONBoard jsonBoard = new JSONBoard();
        List<JSONBoard.JSONGoal> goalList = new ArrayList<>();
        for (Goal goal : goals) {
            if (goal == null) {
                showError("The board is not full.", errorX, errorY);
                return;
            }

            JSONBoard.JSONGoal jsonGoal = new JSONBoard.JSONGoal();
            jsonGoal.id = goal.getId();
            if (goal.hasData()) {
                jsonGoal.data = goal.getData();
            }
            goalList.add(jsonGoal);
        }
        jsonBoard.goals = goalList;

        if (new HashSet<>(goals).size() < goals.size()) {
            showError("Some goals are duplicated, fix and try again.", errorX, errorY);
            return;
        }

        String boardName = BoardBuilderData.INSTANCE.getTitle().trim();
        if (boardName.isBlank()) {
            boardName = "Custom Board";
        }

        try {
            boardName = BoardBuilderIO.INSTANCE.getSuitableName(boardName);
            BoardBuilderIO.INSTANCE.saveBoard(boardName, jsonBoard);
        } catch (IOException e) {
            showError("Error while saving board. Check logs.", errorX, errorY);
            Lockout.error(e);
            return;
        }

        String finalBoardName = boardName;
        Text openBoardFile = Text.literal("[Open file]").styled(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, BoardBuilderIO.INSTANCE.getBoardPath(finalBoardName).toFile().getAbsolutePath()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open board file.")))
                        .withFormatting(Formatting.WHITE)
        );
        Text openBoardsDirectory = Text.literal("[View all boards]").styled(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, BoardBuilderIO.DIRECTORY.toFile().getAbsolutePath()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open boards directory.")))
                        .withFormatting(Formatting.WHITE)
        );
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Saved custom board as " + boardName + BoardBuilderIO.FILE_EXTENSION + "!\n").formatted(Formatting.GREEN).append(openBoardFile).append(" ").append(openBoardsDirectory), false);
        close();
    }

    private void showError(String message, int x, int y) {
        saveErrorTextWidget = new TextWidget(Text.of(message), textRenderer);
        saveErrorTextWidget.setTextColor(Color.RED.getRGB());
        saveErrorTextWidget.setPosition(x, y);
        this.addDrawableChild(saveErrorTextWidget);
    }

    @Override
    public void close() {
        closeSearch();
        super.close();
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);

        drawCenterBoard(context, mouseX, mouseY);

        titleTextField.setSuggestion(titleTextField.getText().isEmpty() ? "Board Name" : null);
        if (displaySearch) {
            searchTextField.setSuggestion(searchTextField.getText().isEmpty() ? "Search goals.." : null);
        }
    }

    private static final int LEFT_MOUSE_BUTTON = 0;
    private static final int RIGHT_MOUSE_BUTTON = 1;

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Optional<Integer> hoveredIdx = Utility.getBoardHoveredIndex(BoardBuilderData.INSTANCE.size(), width, height, (int) mouseX, (int) mouseY);
        if ((button == LEFT_MOUSE_BUTTON || button == RIGHT_MOUSE_BUTTON) && hoveredIdx.isPresent()) {
            Goal goal = BoardBuilderData.INSTANCE.getGoals().get(hoveredIdx.get());
            if (button == RIGHT_MOUSE_BUTTON && goal != null && goal.hasData()) {
                openEditData(hoveredIdx.get());
            } else {
                openSearch(hoveredIdx.get());
            }
            return true;
        } else {
            return super.mouseClicked(mouseX, mouseY, button);
        }
    }

    public void openSearch(int hoveredIdx) {
        displayEditData = false;
        displaySearch = true;

        BoardBuilderData.INSTANCE.setModifyingIdx(hoveredIdx);
        CENTER_OFFSET = 100;

        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        clearAndInit();
    }

    public void closeSearch() {
        displaySearch = false;

        BoardBuilderData.INSTANCE.setModifyingIdx(null);
        CENTER_OFFSET = 0;

        this.boardBuilderSearchWidget = null;
        this.closeSearchButton = null;
        this.searchTextField = null;

        clearAndInit();
    }

    public void openEditData(int hoveredIdx) {
        displaySearch = false;
        displayEditData = true;

        BoardBuilderData.INSTANCE.setModifyingIdx(hoveredIdx);
        CENTER_OFFSET = 50;

        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        clearAndInit();
    }

    public void closeEditData() {
        displayEditData = false;

        BoardBuilderData.INSTANCE.setModifyingIdx(null);
        CENTER_OFFSET = 0;

        this.saveDataButton = null;
        this.closeEditDataButton = null;
        this.editDataErrorTextWidget = null;

        clearAndInit();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void drawCenterBoard(DrawContext context, int mouseX, int mouseY) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int size = BoardBuilderData.INSTANCE.size();

        int boardWidth = 2 * GUI_CENTER_PADDING + size * GUI_CENTER_SLOT_SIZE;
        int boardHeight = 2 * GUI_CENTER_PADDING + size * GUI_CENTER_SLOT_SIZE;
        int x = width / 2 - boardWidth / 2 - CENTER_OFFSET;
        int y = height / 2 - boardHeight / 2;

        context.drawGuiTexture(RenderLayer::getGuiTextured, GUI_CENTER_IDENTIFIER, x, y, boardWidth, boardHeight);

        x += GUI_CENTER_PADDING + 1;
        y += GUI_CENTER_PADDING + 1;
        final int startX = x;

        Optional<Integer> hoveredIdx = Utility.getBoardHoveredIndex(BoardBuilderData.INSTANCE.size(), width, height, mouseX, mouseY);
        Integer editingIdx = BoardBuilderData.INSTANCE.getModifyingIdx();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                int idx = j + size * i;
                Goal goal = BoardBuilderData.INSTANCE.getGoals().get(idx);
                if (goal != null) {
                    boolean success = false;
                    if (goal instanceof CustomTextureRenderer customTextureRenderer) {
                        success = customTextureRenderer.renderTexture(context, x, y, LockoutClient.CURRENT_TICK);
                    }
                    if (!success) {
                        context.drawItem(goal.getTextureItemStack(), x, y);
                        context.drawStackOverlay(textRenderer, goal.getTextureItemStack(), x, y);
                    }
                }

                if (hoveredIdx.isPresent() && hoveredIdx.get() == idx) {
                    context.fill(x, y, x + 16, y + 16, 400, GUI_CENTER_HOVERED_COLOR);
                }
                if (editingIdx != null && editingIdx == idx) {
                    drawBorder(context, x - 1, y - 1, GUI_SLOT_SIZE, GUI_SLOT_SIZE, Color.RED.getRGB());
                }
                if (hoveredIdx.isPresent() && hoveredIdx.get() == idx) {
                    if (goal != null) {
                        List<OrderedText> tooltip = new ArrayList<>();
                        tooltip.add(Text.of(goal.getGoalName()).asOrderedText());
                        if (goal.hasData()) {
                            tooltip.add(Text.literal("Right-click to edit data.").formatted(Formatting.GRAY, Formatting.ITALIC).asOrderedText());
                        }
                        context.drawOrderedTooltip(textRenderer, tooltip, mouseX, mouseY);
                    }
                }

                x += GUI_CENTER_SLOT_SIZE;
            }
            y += GUI_CENTER_SLOT_SIZE;
            x = startX;
        }
    }

    private static void drawBorder(DrawContext context, int x, int y, int width, int height, int color) {
        context.fill(x, y, x + width, y + 1, color);
        context.fill(x, y + height - 1, x + width, y + height, color);
        context.fill(x, y + 1, x + 1, y + height - 1, color);
        context.fill(x + width - 1, y + 1, x + width, y + height - 1, color);
    }

}