package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.Map;
import java.util.Set;

public abstract class ConsumeItemGoal extends Goal {

    public ConsumeItemGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return getItem().getDefaultStack();
    }

    public abstract Item getItem();

}
