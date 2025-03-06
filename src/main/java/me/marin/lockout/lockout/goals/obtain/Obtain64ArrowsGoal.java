package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.Utility;
import me.marin.lockout.lockout.interfaces.ObtainAllItemsGoal;
import me.marin.lockout.lockout.interfaces.RequiresAmount;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class Obtain64ArrowsGoal extends ObtainAllItemsGoal implements RequiresAmount {

    private static final ItemStack ITEM_STACK = Items.ARROW.getDefaultStack();
    static {
        ITEM_STACK.setCount(64);
    }
    private static final List<Item> ITEMS = List.of(ITEM_STACK.getItem());

    public Obtain64ArrowsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain 64 Arrows";
    }

    @Override
    public int getAmount() {
        return 64;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        Utility.drawStackCount(context, x, y, String.valueOf(getAmount()));
        return true;
    }

}
