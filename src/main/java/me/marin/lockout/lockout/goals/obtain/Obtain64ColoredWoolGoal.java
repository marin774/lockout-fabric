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

import java.lang.reflect.Field;
import java.util.List;

public class Obtain64ColoredWoolGoal extends ObtainAllItemsGoal implements RequiresAmount {

    private final ItemStack ITEM_STACK;
    private final List<Item> ITEMS;

    private final String GOAL_NAME;

    public Obtain64ColoredWoolGoal(String id, String data) {
        super(id, data);
        System.out.println(data);
        DyeColor DYE_COLOR = GoalDataConstants.getDyeColor(data);

        GOAL_NAME = "Obtain 64 " + GoalDataConstants.getDyeColorFormatted(DYE_COLOR) + " Wool";
        ITEMS = List.of(getWoolColor(data));
        ITEM_STACK = getWoolColor(data).getDefaultStack();
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

    public static Item getWoolColor(String colorString) {
        return switch (colorString) {
            default -> null;
            case "white" -> Items.WHITE_WOOL;
            case "orange" -> Items.ORANGE_WOOL;
            case "magenta" -> Items.MAGENTA_WOOL;
            case "light_blue" -> Items.LIGHT_BLUE_WOOL;
            case "yellow" -> Items.YELLOW_WOOL;
            case "lime" -> Items.LIME_WOOL;
            case "pink" -> Items.PINK_WOOL;
            case "gray" -> Items.GRAY_WOOL;
            case "light_gray" -> Items.LIGHT_GRAY_WOOL;
            case "cyan" -> Items.CYAN_WOOL;
            case "purple" -> Items.PURPLE_WOOL;
            case "blue" -> Items.BLUE_WOOL;
            case "brown" -> Items.BROWN_WOOL;
            case "green" -> Items.GREEN_WOOL;
            case "red" -> Items.RED_WOOL;
            case "black" -> Items.BLACK_WOOL;
        };
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

}

