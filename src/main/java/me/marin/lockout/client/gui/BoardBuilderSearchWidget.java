package me.marin.lockout.client.gui;

import me.marin.lockout.generator.GoalDataGenerator;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.GoalRegistry;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.AlwaysSelectedEntryListWidget;
import net.minecraft.client.gui.widget.ScrollableWidget;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.text.WordUtils;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@Environment(EnvType.CLIENT)
public class BoardBuilderSearchWidget extends ScrollableWidget {

    private static final int MARGIN_X = 3;
    private static final int MARGIN_Y = 3;
    private static final int ITEM_HEIGHT = 18;
    private static final Map<String, GoalEntry> registeredGoals = new LinkedHashMap<>();

    private int rowWidth;
    private int left;
    private int right;
    private int top;
    private static GoalEntry hovered;
    private List<GoalEntry> visibleGoals;

    public BoardBuilderSearchWidget(int x, int y, int width, int height, Text text) {
        super(x, y, width, height, text);
        for (String id : GoalRegistry.INSTANCE.getRegisteredGoals()) {
            registeredGoals.putIfAbsent(id, new BoardBuilderSearchWidget.GoalEntry(id));
        }
        visibleGoals = new ArrayList<>(registeredGoals.values());
        searchUpdated(BoardBuilderData.INSTANCE.getSearch());
    }

    public void setScrollY(double scrollY) {
        super.setScrollY(scrollY);
    }

    public double getScrollY() {
        return super.getScrollY();
    }

    @Override
    protected int getContentsHeight() {
        return visibleGoals.size() * ITEM_HEIGHT;
    }

    @Override
    protected double getDeltaYPerScroll() {
        return ITEM_HEIGHT / 2.0;
    }

    @Override
    protected void renderContents(DrawContext context, int mouseX, int mouseY, float delta) {
        this.rowWidth = getWidth() - MARGIN_X * 2;
        this.left = getX() + MARGIN_X;
        this.top = getY();
        this.right = getX() + getWidth() - MARGIN_X;

        hovered = this.isMouseOver(mouseX, mouseY) ? this.getEntryAtPosition(mouseX, mouseY) : null;

        context.enableScissor(this.left - 1, this.top, this.right + 1, getY() + getHeight());

        int y = 4;
        int idx = 0;
        for (GoalEntry goalEntry : visibleGoals) {
            goalEntry.render(context, idx++,getY() + y,getX() + MARGIN_X, rowWidth, 18, mouseX, mouseY, Objects.equals(goalEntry, hovered), delta);
            y += 18;
        }

        context.disableScissor();
    }

    protected final GoalEntry getEntryAtPosition(double x, double y) {
        int halfRowWidth = this.rowWidth / 2;
        int centerX = this.left + this.width / 2;
        int left = centerX - halfRowWidth;
        int right = centerX + halfRowWidth;
        int scrolledY = MathHelper.floor(y - (double)this.top) + (int)getScrollY() - MARGIN_Y;
        int idx = scrolledY / ITEM_HEIGHT;
        if (x < (this.right + MARGIN_X) && x >= (double) left && x <= (double) right && idx >= 0 && scrolledY >= 0 && idx < visibleGoals.size()) {
            return registeredGoals.get(visibleGoals.get(idx).goal.getId());
        }
        return null;
    }

    public void searchUpdated(String search) {
        setScrollY(0);
        visibleGoals = new ArrayList<>(registeredGoals.values()).stream().filter(goalEntry -> goalEntry.displayName.toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList());
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (hovered != null) {
            BoardBuilderData.INSTANCE.setGoal(hovered.goal);
            MinecraftClient.getInstance().getSoundManager().play(PositionedSoundInstance.master(SoundEvents.UI_BUTTON_CLICK, 1.0f));
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {}

    public static final class GoalEntry extends AlwaysSelectedEntryListWidget.Entry<GoalEntry> {

        private final Goal goal;
        public final String displayName;

        public GoalEntry(String id) {
            Optional<GoalDataGenerator> gen = GoalRegistry.INSTANCE.getDataGenerator(id);

            // generate random data
            String data = gen.map(g -> g.generateData(new ArrayList<>(GoalDataGenerator.ALL_DYES))).orElse(GoalDataConstants.DATA_NONE);
            this.goal = GoalRegistry.INSTANCE.newGoal(id, data);

            this.displayName = gen.isEmpty() ? goal.getGoalName() : "[*] " + WordUtils.capitalize(goal.getId().replace("_", " ").toLowerCase(), ' ');
        }


        @Override
        public Text getNarration() {
            return Text.empty();
        }

        @Override
        public void render(DrawContext context, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean hovered, float tickDelta) {
            TextRenderer textRenderer = MinecraftClient.getInstance().textRenderer;

            goal.render(context, textRenderer, x, y);
            context.drawTextWithShadow(textRenderer, displayName, x + 18, y + 5, Color.WHITE.getRGB());
            if (hovered) {
                context.drawBorder(x - 1, y - 1, entryWidth + 2, entryHeight, Color.LIGHT_GRAY.getRGB());
            }
        }
    }


}
