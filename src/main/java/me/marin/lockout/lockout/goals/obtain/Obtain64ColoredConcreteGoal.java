package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import me.marin.lockout.lockout.interfaces.RequiresAmount;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

import java.util.List;

public class Obtain64ColoredConcreteGoal extends ObtainAllItemsGoal implements RequiresAmount {

    private final ItemStack ITEM_STACK;
    private final List<Item> ITEMS;

    private final String GOAL_NAME;

    public Obtain64ColoredConcreteGoal(String id, String data) {
        super(id, data);
        DyeColor DYE_COLOR = GoalDataConstants.getDyeColor(data);

        GOAL_NAME = "Obtain 64 " + GoalDataConstants.getDyeColorFormatted(DYE_COLOR) + " Concrete";
        ITEMS = List.of(getConcreteColor(data));
        ITEM_STACK = getConcreteColor(data).getDefaultStack();
        ITEM_STACK.setCount(64);
    }

    @Override
    public int getAmount() {
        return 64;
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return GOAL_NAME;
    }

    public static Item getConcreteColor(String colorString) {
        return switch (colorString) {
            default -> null;
            case "white" -> Items.WHITE_CONCRETE;
            case "orange" -> Items.ORANGE_CONCRETE;
            case "magenta" -> Items.MAGENTA_CONCRETE;
            case "light_blue" -> Items.LIGHT_BLUE_CONCRETE;
            case "yellow" -> Items.YELLOW_CONCRETE;
            case "lime" -> Items.LIME_CONCRETE;
            case "pink" -> Items.PINK_CONCRETE;
            case "gray" -> Items.GRAY_CONCRETE;
            case "light_gray" -> Items.LIGHT_GRAY_CONCRETE;
            case "cyan" -> Items.CYAN_CONCRETE;
            case "purple" -> Items.PURPLE_CONCRETE;
            case "blue" -> Items.BLUE_CONCRETE;
            case "brown" -> Items.BROWN_CONCRETE;
            case "green" -> Items.GREEN_CONCRETE;
            case "red" -> Items.RED_CONCRETE;
            case "black" -> Items.BLACK_CONCRETE;
        };
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

}

