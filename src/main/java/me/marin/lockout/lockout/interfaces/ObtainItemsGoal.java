package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CycleItemTexturesProvider;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;

public abstract class ObtainItemsGoal extends Goal implements CycleItemTexturesProvider {

    public ObtainItemsGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<Item> getItems();

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    @Override
    public List<Item> getItemsToDisplay() {
        return getItems();
    }

    public abstract boolean satisfiedBy(PlayerInventory playerInventory);

}
