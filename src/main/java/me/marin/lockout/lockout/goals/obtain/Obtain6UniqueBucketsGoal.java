package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainSomeOfTheItemsGoal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class Obtain6UniqueBucketsGoal extends ObtainSomeOfTheItemsGoal {

    private static final ItemStack ITEM_STACK = Items.BUCKET.getDefaultStack();
    static {
        ITEM_STACK.setCount(6);
    }
    private static final List<Item> ITEMS = List.of(
            Items.BUCKET,
            Items.WATER_BUCKET,
            Items.COD_BUCKET,
            Items.SALMON_BUCKET,
            Items.LAVA_BUCKET,
            Items.MILK_BUCKET,
            Items.TROPICAL_FISH_BUCKET,
            Items.PUFFERFISH_BUCKET,
            Items.AXOLOTL_BUCKET,
            Items.POWDER_SNOW_BUCKET,
            Items.TADPOLE_BUCKET
    );

    public Obtain6UniqueBucketsGoal(String id, String data) {
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
        return "Obtain 6 Unique Buckets";
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

}
