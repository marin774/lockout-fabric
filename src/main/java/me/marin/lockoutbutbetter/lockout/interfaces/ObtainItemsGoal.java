package me.marin.lockoutbutbetter.lockout.interfaces;

import me.marin.lockoutbutbetter.lockout.Goal;
import me.marin.lockoutbutbetter.lockout.texture.CustomTextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ObtainItemsGoal extends Goal implements CustomTextureProvider {

    public ObtainItemsGoal(String id) {
        super(id);
    }

    public abstract List<Item> getItems();

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    @Override
    public String getGoalName() {
        return null;
    }

    @Override
    public void renderTexture(DrawContext context, int x, int y, int tick) {
        // Select item
        List<Item> itemList = new ArrayList<>(getItems());

        int mod = tick % (60 * itemList.size());
        context.drawItem(itemList.get(mod / 60).getDefaultStack(), x, y);
    }
}
