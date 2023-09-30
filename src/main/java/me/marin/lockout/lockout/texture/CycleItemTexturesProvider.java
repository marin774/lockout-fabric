package me.marin.lockout.lockout.texture;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public interface CycleItemTexturesProvider extends CustomTextureRenderer {

    List<Item> getItemsToDisplay();

    @Override
    default boolean renderTexture(DrawContext context, int x, int y, int tick) {
        int mod = tick % (60 * getItemsToDisplay().size());
        context.drawItem(getItemsToDisplay().get(mod / 60).getDefaultStack(), x, y);
        return true;
    }

}
