package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainSomeOfTheItemsGoal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class Obtain6UniqueFlowersGoal extends ObtainSomeOfTheItemsGoal {

    private static final ItemStack ITEM_STACK = Items.DANDELION.getDefaultStack();
    static {
        ITEM_STACK.setCount(6);
    }
    private static final List<Item> ITEMS = List.of(
            Items.DANDELION,
            Items.POPPY,
            Items.BLUE_ORCHID,
            Items.ALLIUM,
            Items.AZURE_BLUET,
            Items.RED_TULIP,
            Items.ORANGE_TULIP,
            Items.WHITE_TULIP,
            Items.PINK_TULIP,
            Items.OXEYE_DAISY,
            Items.CORNFLOWER,
            Items.LILY_OF_THE_VALLEY,
            Items.TORCHFLOWER,
            Items.WITHER_ROSE,
            Items.SUNFLOWER,
            Items.LILAC,
            Items.ROSE_BUSH,
            Items.PEONY
    );

    public Obtain6UniqueFlowersGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public int getAmount() {
        return 6;
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain 6 Unique Flowers";
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

}
