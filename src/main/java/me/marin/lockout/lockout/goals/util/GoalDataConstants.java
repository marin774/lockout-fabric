package me.marin.lockout.lockout.goals.util;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.DyeColor;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@SuppressWarnings("unused")
public class GoalDataConstants {

    public static final String DATA_NONE = "null";

    public static final String DATA_SEPARATOR = "&";
    public static final String DATA_LEATHER_CHESTPLATE = "leather_chestplate";
    public static final String DATA_LEATHER_HELMET = "leather_helmet";
    public static final String DATA_LEATHER_LEGGINGS = "leather_leggings";
    public static final String DATA_LEATHER_BOOTS = "leather_boots";
    public static final List<String> DATA_LEATHER_ARMOR = List.of(DATA_LEATHER_HELMET, DATA_LEATHER_CHESTPLATE, DATA_LEATHER_LEGGINGS, DATA_LEATHER_BOOTS);

    public static final String DATA_COLOR_BLACK = "black";
    public static final String DATA_COLOR_WHITE = "white";
    public static final String DATA_COLOR_RED = "red";
    public static final String DATA_COLOR_YELLOW = "yellow";
    public static final String DATA_COLOR_PINK = "pink";
    public static final String DATA_COLOR_PURPLE = "purple";
    public static final String DATA_COLOR_LIME = "lime";
    public static final String DATA_COLOR_CYAN = "cyan";
    public static final String DATA_COLOR_LIGHT_GRAY = "light_gray";
    public static final String DATA_COLOR_GRAY = "gray";
    public static final String DATA_COLOR_GREEN = "green";
    public static final String DATA_COLOR_BLUE = "blue";
    public static final String DATA_COLOR_LIGHT_BLUE = "light_blue";
    public static final String DATA_COLOR_BROWN = "brown";
    public static final String DATA_COLOR_MAGENTA = "magenta";
    public static final String DATA_COLOR_ORANGE = "orange";

    public static Item getLeatherArmor(String leatherArmorString) {
        return switch (leatherArmorString) {
            default -> null;
            case DATA_LEATHER_HELMET -> Items.LEATHER_HELMET;
            case DATA_LEATHER_CHESTPLATE -> Items.LEATHER_CHESTPLATE;
            case DATA_LEATHER_LEGGINGS -> Items.LEATHER_LEGGINGS;
            case DATA_LEATHER_BOOTS -> Items.LEATHER_BOOTS;
        };
    }

    public static DyeColor getDyeColor(String dyeColorString) {
        return switch (dyeColorString) {
            default -> null;
            case "white" -> DyeColor.WHITE;
            case "orange" -> DyeColor.ORANGE;
            case "magenta" -> DyeColor.MAGENTA;
            case "light_blue" -> DyeColor.LIGHT_BLUE;
            case "yellow" -> DyeColor.YELLOW;
            case "lime" -> DyeColor.LIME;
            case "pink" -> DyeColor.PINK;
            case "gray" -> DyeColor.GRAY;
            case "light_gray" -> DyeColor.LIGHT_GRAY;
            case "cyan" -> DyeColor.CYAN;
            case "purple" -> DyeColor.PURPLE;
            case "blue" -> DyeColor.BLUE;
            case "brown" -> DyeColor.BROWN;
            case "green" -> DyeColor.GREEN;
            case "red" -> DyeColor.RED;
            case "black" -> DyeColor.BLACK;
        };
    }

    public static String getDyeColorDataString(DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE -> GoalDataConstants.DATA_COLOR_WHITE;
            case ORANGE -> GoalDataConstants.DATA_COLOR_ORANGE;
            case MAGENTA -> GoalDataConstants.DATA_COLOR_MAGENTA;
            case LIGHT_BLUE -> GoalDataConstants.DATA_COLOR_LIGHT_BLUE;
            case YELLOW -> GoalDataConstants.DATA_COLOR_YELLOW;
            case LIME -> GoalDataConstants.DATA_COLOR_LIME;
            case PINK -> GoalDataConstants.DATA_COLOR_PINK;
            case GRAY -> GoalDataConstants.DATA_COLOR_GRAY;
            case LIGHT_GRAY -> GoalDataConstants.DATA_COLOR_LIGHT_GRAY;
            case CYAN -> GoalDataConstants.DATA_COLOR_CYAN;
            case PURPLE -> GoalDataConstants.DATA_COLOR_PURPLE;
            case BLUE -> GoalDataConstants.DATA_COLOR_BLUE;
            case BROWN -> GoalDataConstants.DATA_COLOR_BROWN;
            case GREEN -> GoalDataConstants.DATA_COLOR_GREEN;
            case RED -> GoalDataConstants.DATA_COLOR_RED;
            case BLACK -> GoalDataConstants.DATA_COLOR_BLACK;
        };
    }

    public static int getDyeColorValue(DyeColor dyeColor) {
        return switch (dyeColor) {
            case WHITE -> 16383998;
            case ORANGE -> 16351261;
            case MAGENTA -> 13061821;
            case LIGHT_BLUE -> 3847130;
            case YELLOW -> 16701501;
            case LIME -> 8439583;
            case PINK -> 15961002;
            case GRAY -> 4673362;
            case LIGHT_GRAY -> 10329495;
            case CYAN -> 1481884;
            case PURPLE -> 8991416;
            case BLUE -> 3949738;
            case BROWN -> 8606770;
            case GREEN -> 6192150;
            case RED -> 11546150;
            case BLACK -> 1908001;
        };
    }

    public static String getDyeColorFormatted(DyeColor dyeColor) {
        return StringUtils.capitalize(dyeColor.toString().replaceAll("_", " "));
    }

    public static String getArmorPieceFormatted(String leatherArmorString) {
        return StringUtils.capitalize(leatherArmorString.replaceAll("_", " "));
    }

}
