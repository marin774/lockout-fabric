package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class PutWolfArmorOnWolfGoal extends Goal {

    private static final ItemStack ITEM = Items.WOLF_ARMOR.getDefaultStack();

    public PutWolfArmorOnWolfGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Put Wolf Armor on Wolf";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM;
    }

}
