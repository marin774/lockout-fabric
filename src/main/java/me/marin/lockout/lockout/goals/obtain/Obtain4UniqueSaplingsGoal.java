package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainSomeOfTheItemsGoal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class Obtain4UniqueSaplingsGoal extends ObtainSomeOfTheItemsGoal {

    private static final ItemStack ITEM_STACK = Items.OAK_SAPLING.getDefaultStack();
    static {
        ITEM_STACK.setCount(4);
    }
    private static final List<Item> ITEMS = List.of(
            Items.OAK_SAPLING,
            Items.ACACIA_SAPLING,
            Items.BIRCH_SAPLING,
            Items.CHERRY_SAPLING,
            Items.DARK_OAK_SAPLING,
            Items.JUNGLE_SAPLING,
            Items.SPRUCE_SAPLING,
            Items.MANGROVE_PROPAGULE
    );

    public Obtain4UniqueSaplingsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public int getAmount() {
        return 4;
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain 4 Unique Saplings";
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        context.drawStackOverlay(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

}