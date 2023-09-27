package me.marin.lockoutbutbetter.lockout.goals.mine;

import me.marin.lockoutbutbetter.lockout.interfaces.MineBlockGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class MineDiamondOreGoal extends MineBlockGoal {

    private static final List<Item> ITEMS = List.of(Items.DIAMOND_ORE, Items.DEEPSLATE_DIAMOND_ORE);
    private static final Item ITEM = Items.DIAMOND_ORE;

    public MineDiamondOreGoal(String id) {
        super(id);
    }

    @Override
    public String getGoalName() {
        return "Mine Diamond Ore";
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
