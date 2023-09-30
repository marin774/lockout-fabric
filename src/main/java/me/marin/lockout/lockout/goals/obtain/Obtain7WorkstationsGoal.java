package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.lockout.interfaces.ObtainSomeOfTheItemsGoal;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class Obtain7WorkstationsGoal extends ObtainSomeOfTheItemsGoal {

    private static final ItemStack ITEM_STACK = Items.BLAST_FURNACE.getDefaultStack();
    static {
        ITEM_STACK.setCount(7);
    }
    private static final List<Item> ITEMS = List.of(
            Items.BLAST_FURNACE,
            Items.SMOKER,
            Items.CARTOGRAPHY_TABLE,
            Items.BREWING_STAND,
            Items.BARREL,
            Items.COMPOSTER,
            Items.FLETCHING_TABLE,
            Items.CAULDRON,
            Items.LECTERN,
            Items.STONECUTTER,
            Items.LOOM,
            Items.SMITHING_TABLE,
            Items.GRINDSTONE
    );

    public Obtain7WorkstationsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public int getAmount() {
        return 7;
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public String getGoalName() {
        return "Obtain 7 Unique Workstations";
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        super.renderTexture(context, x, y, tick);
        context.drawItemInSlot(MinecraftClient.getInstance().textRenderer, ITEM_STACK, x, y);
        return true;
    }

}
