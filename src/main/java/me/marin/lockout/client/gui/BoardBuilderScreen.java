package me.marin.lockout.client.gui;

import me.marin.lockout.Constants;
import me.marin.lockout.Utility;
import me.marin.lockout.client.LockoutClient;
import me.marin.lockout.generator.GoalDataGenerator;
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
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.*;
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
    private TextFieldWidget searchTextField;
    private BoardBuilderGoalsWidget boardBuilderGoalsWidget;
    private ButtonWidget saveDataButton;
    private ButtonWidget closeEditDataButton;
    private TextWidget editDataErrorTextWidget;


    public BoardBuilderScreen(Text title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int centerX = width / 2;
        int centerY = height / 2;

        titleTextField = new TextFieldWidget(textRenderer, centerX - 60 - CENTER_OFFSET, centerY - 80, 120, 18, Text.empty());
        titleTextField.setChangedListener(s -> {
            BoardBuilderData.INSTANCE.setTitle(s);
        });
        titleTextField.setText(BoardBuilderData.INSTANCE.getTitle());
        this.addDrawableChild(titleTextField);

        saveButton = ButtonWidget.builder(Text.of("Save Board"), (b) -> {
            saveGoals(10, height - 45);
        }).width(85).position(10, height - 30).build();
        this.addDrawableChild(saveButton);

        closeButton = ButtonWidget.builder(Text.of("Close"), (b) -> {
            close();
        }).width(50).position(width - 50 - 10, saveButton.getY()).build();
        this.addDrawableChild(closeButton);

        clearBoardButton = ButtonWidget.builder(Text.of("Clear Board"), (b) -> {
            BoardBuilderData.INSTANCE.clear();
            closeEditData();
            closeSearch();
        }).width(85).position(closeButton.getX() - 85 - 10, saveButton.getY()).build();
        this.addDrawableChild(clearBoardButton);

        if (displaySearch) {
            double scrollY = boardBuilderGoalsWidget == null ? 0 : boardBuilderGoalsWidget.getScrollY();
            boardBuilderGoalsWidget = new BoardBuilderGoalsWidget(
                    centerX + 90 - CENTER_OFFSET,
                    40,
                    width / 2 - 125 + CENTER_OFFSET,
                    height - 40 * 2, Text.empty());
            boardBuilderGoalsWidget.setScrollY(scrollY);
            this.addDrawableChild(boardBuilderGoalsWidget);

            closeSearchButton = ButtonWidget.builder(Text.of("<"), (b) -> {
                closeSearch();
            }).tooltip(Tooltip.of(Text.of("Close search"))).width(20).position(boardBuilderGoalsWidget.getX(), boardBuilderGoalsWidget.getY() - 21).build();
            this.addDrawableChild(closeSearchButton);

            searchTextField = new TextFieldWidget(textRenderer, closeSearchButton.getX() + closeSearchButton.getWidth() + 1 + 5, closeSearchButton.getY() + 1, boardBuilderGoalsWidget.getWidth() - closeSearchButton.getWidth() - 2 - 5, 18, Text.empty());
            searchTextField.setChangedListener(s -> {
                boardBuilderGoalsWidget.searchUpdated(s);
            });
            this.addDrawableChild(searchTextField);
        }
        if (displayEditData) {
            Goal goal = BoardBuilderData.INSTANCE.getEditingGoal();
            var generators = GoalRegistry.INSTANCE.getDataGenerator(goal.getId()).getGenerators();
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
        StringBuilder sb = new StringBuilder();
        for (Goal goal : goals) {
            if (goal == null) {
                showError("The board is not full.", errorX, errorY);
                return;
            }
            if (!sb.isEmpty()) {
                sb.append("\n");
            }

            String data = goal.getData();

            sb.append(goal.getId());
            if (data != null && !data.isBlank()) {
                sb.append(" ").append(data);
            }
        }

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
            BoardBuilderIO.INSTANCE.saveBoard(boardName, sb.toString());
        } catch (IOException e) {
            showError("Error while saving board. Check logs.", errorX, errorY);
            e.printStackTrace();
            return;
        }

        String finalBoardName = boardName;
        Text openBoardFile = Text.literal("[Open file]").styled(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, BoardBuilderIO.INSTANCE.getBoardPath(finalBoardName).toFile().getAbsolutePath()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open boards directory.")))
                        .withFormatting(Formatting.WHITE)
        );
        Text openBoardsDirectory = Text.literal("[View all boards]").styled(style ->
                style.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, BoardBuilderIO.DIRECTORY.toFile().getAbsolutePath()))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("Click to open boards directory.")))
                        .withFormatting(Formatting.WHITE)
        );
        MinecraftClient.getInstance().player.sendMessage(Text.literal("Saved board as " + boardName + ".txt!\n").formatted(Formatting.GREEN).append(openBoardFile).append(" ").append(openBoardsDirectory));
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
        this.renderBackground(context);

        drawCenterBoard(context, mouseX, mouseY);

        titleTextField.setSuggestion(titleTextField.getText().isEmpty() ? "Board Name" : null);
        if (displaySearch) {
            searchTextField.setSuggestion(searchTextField.getText().isEmpty() ? "Search goals.." : null);
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Optional<Integer> hoveredIdx = Utility.getBoardHoveredIndex(width, height, (int) mouseX, (int) mouseY);
        if ((button == 0 || button == 1) && hoveredIdx.isPresent()) {
            Goal goal = BoardBuilderData.INSTANCE.getGoals().get(hoveredIdx.get());
            if (button == 1 && goal != null && goal.getData() != null) {
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

        BoardBuilderData.INSTANCE.setEditingIdx(hoveredIdx);
        CENTER_OFFSET = 100;

        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        clearAndInit();
    }

    public void closeSearch() {
        displaySearch = false;

        BoardBuilderData.INSTANCE.setEditingIdx(null);
        CENTER_OFFSET = 0;

        this.boardBuilderGoalsWidget = null;
        this.closeSearchButton = null;
        this.searchTextField = null;

        clearAndInit();
    }

    public void openEditData(int hoveredIdx) {
        displaySearch = false;
        displayEditData = true;

        BoardBuilderData.INSTANCE.setEditingIdx(hoveredIdx);
        CENTER_OFFSET = 50;

        MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        clearAndInit();
    }

    public void closeEditData() {
        displayEditData = false;

        BoardBuilderData.INSTANCE.setEditingIdx(null);
        CENTER_OFFSET = 0;

        this.saveDataButton = null;
        this.closeEditDataButton = null;
        this.editDataErrorTextWidget = null;

        clearAndInit();
    }

    @Override
    public void tick() {
        titleTextField.tick();
    }

    @Override
    public boolean shouldCloseOnEsc() {
        return false;
    }

    public void drawCenterBoard(DrawContext context, int mouseX, int mouseY) {
        TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

        int x = width / 2 - Constants.GUI_CENTER_WIDTH / 2 - CENTER_OFFSET;
        int y = height / 2 - Constants.GUI_CENTER_HEIGHT / 2;

        context.drawTexture(GUI_CENTER_IDENTIFIER, x, y, 0, 0, GUI_CENTER_WIDTH, GUI_CENTER_HEIGHT, GUI_CENTER_WIDTH, GUI_CENTER_HEIGHT);

        x += GUI_CENTER_FIRST_ITEM_OFFSET_X;
        y += GUI_CENTER_FIRST_ITEM_OFFSET_Y;
        final int startX = x;

        Optional<Integer> hoveredIdx = Utility.getBoardHoveredIndex(width, height, mouseX, mouseY);
        Integer editingIdx = BoardBuilderData.INSTANCE.getEditingIdx();

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int idx = j + 5 * i;
                Goal goal = BoardBuilderData.INSTANCE.getGoals().get(idx);
                if (goal != null) {
                    boolean success = false;
                    if (goal instanceof CustomTextureRenderer customTextureRenderer) {
                        success = customTextureRenderer.renderTexture(context, x, y, LockoutClient.CURRENT_TICK);
                    }
                    if (!success) {
                        context.drawItem(goal.getTextureItemStack(), x, y);
                        context.drawItemInSlot(textRenderer, goal.getTextureItemStack(), x, y);
                    }
                }

                if (hoveredIdx.isPresent() && hoveredIdx.get() == idx) {
                    context.fill(x, y, x + 16, y + 16, 400, -2130706433);
                }
                if (editingIdx != null && editingIdx == idx) {
                    drawBorder(context, x - 1, y - 1, 18, 18, Color.RED.getRGB());
                }
                if (hoveredIdx.isPresent() && hoveredIdx.get() == idx) {
                    if (goal != null) {
                        List<OrderedText> lore = new ArrayList<>();
                        lore.add(Text.of(goal.getGoalName()).asOrderedText());
                        if (goal.getData() != null) {
                            lore.add(Text.literal("Right-click to edit data.").formatted(Formatting.GRAY, Formatting.ITALIC).asOrderedText());
                        }
                        context.drawOrderedTooltip(textRenderer, lore, mouseX, mouseY);
                    }
                }

                x += GUI_CENTER_ITEM_SLOT_SIZE;
            }
            y += GUI_CENTER_ITEM_SLOT_SIZE;
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