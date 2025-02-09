package me.marin.lockout.lockout.goals.mine;

import me.marin.lockout.lockout.interfaces.MineBlockGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class MineCrafterGoal extends MineBlockGoal {

    private static final List<Item> ITEMS = List.of(Items.CRAFTER);
    private static final Item ITEM = Items.CRAFTER;

    public MineCrafterGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Mine Crafter";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

}
