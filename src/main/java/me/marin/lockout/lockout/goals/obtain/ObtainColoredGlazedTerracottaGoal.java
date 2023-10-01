package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;

import java.util.List;

public class ObtainColoredGlazedTerracottaGoal extends ObtainAllItemsGoal {

    private final List<Item> ITEMS;

    private final String GOAL_NAME;

    public ObtainColoredGlazedTerracottaGoal(String id, String data) throws NoSuchFieldException, IllegalAccessException {
        super(id, data);
        DyeColor DYE_COLOR = GoalDataConstants.getDyeColor(data);

        GOAL_NAME = "Obtain " + GoalDataConstants.getDyeColorFormatted(DYE_COLOR) + " Glazed Terracotta";
        ITEMS = List.of(getGlazedTerracottaColor(data));
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return GOAL_NAME;
    }

    public static Item getGlazedTerracottaColor(String colorString) {
        return switch (colorString) {
            default -> null;
            case "white" -> Items.WHITE_GLAZED_TERRACOTTA;
            case "orange" -> Items.ORANGE_GLAZED_TERRACOTTA;
            case "magenta" -> Items.MAGENTA_GLAZED_TERRACOTTA;
            case "light_blue" -> Items.LIGHT_BLUE_GLAZED_TERRACOTTA;
            case "yellow" -> Items.YELLOW_GLAZED_TERRACOTTA;
            case "lime" -> Items.LIME_GLAZED_TERRACOTTA;
            case "pink" -> Items.PINK_GLAZED_TERRACOTTA;
            case "gray" -> Items.GRAY_GLAZED_TERRACOTTA;
            case "light_gray" -> Items.LIGHT_GRAY_GLAZED_TERRACOTTA;
            case "cyan" -> Items.CYAN_GLAZED_TERRACOTTA;
            case "purple" -> Items.PURPLE_GLAZED_TERRACOTTA;
            case "blue" -> Items.BLUE_GLAZED_TERRACOTTA;
            case "brown" -> Items.BROWN_GLAZED_TERRACOTTA;
            case "green" -> Items.GREEN_GLAZED_TERRACOTTA;
            case "red" -> Items.RED_GLAZED_TERRACOTTA;
            case "black" -> Items.BLACK_GLAZED_TERRACOTTA;
        };
    }

}
