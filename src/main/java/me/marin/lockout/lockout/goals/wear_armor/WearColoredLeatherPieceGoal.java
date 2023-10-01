package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.lockout.interfaces.WearArmorPieceGoal;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DyeColor;

import java.util.List;

public class WearColoredLeatherPieceGoal extends WearArmorPieceGoal {

    private final Item ITEM;
    private final ItemStack DISPLAY_ITEM_STACK;
    private final int COLOR;
    private final String GOAL_NAME;

    public WearColoredLeatherPieceGoal(String id, String data) {
        super(id, data);

        String[] parts = data.split(GoalDataConstants.DATA_SEPARATOR);
        ITEM = GoalDataConstants.getLeatherArmor(parts[0]);
        DyeColor DYE_COLOR = GoalDataConstants.getDyeColor(parts[1]);
        COLOR = GoalDataConstants.getDyeColorValue(DYE_COLOR);

        DISPLAY_ITEM_STACK = ITEM.getDefaultStack();
        ((DyeableArmorItem) ITEM).setColor(DISPLAY_ITEM_STACK, COLOR);

        GOAL_NAME = "Wear " + GoalDataConstants.getDyeColorFormatted(DYE_COLOR) + " Leather " + GoalDataConstants.getArmorPieceFormatted(parts[0]);
    }

    @Override
    public String getGoalName() {
        return GOAL_NAME;
    }

    @Override
    public List<Item> getItems() {
        return List.of(ITEM);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        return false;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        for (ItemStack item : playerInventory.armor) {
            if (item == null) continue;
            if (item.getItem().equals(ITEM)) {
                if (((DyeableArmorItem) ITEM).getColor(item) == COLOR) {
                    return true;
                }
            }
        }
        return false;
    }

}
