package me.marin.lockoutbutbetter.lockout.goals.mine;

import me.marin.lockoutbutbetter.lockout.interfaces.MineBlockGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class MineMobSpawnerGoal extends MineBlockGoal {

    private static final List<Item> ITEMS = List.of(Items.SPAWNER);
    private static final Item ITEM = Items.SPAWNER;

    public MineMobSpawnerGoal(String id) {
        super(id);
    }

    @Override
    public String getGoalName() {
        return "Mine Mob Spawner";
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

