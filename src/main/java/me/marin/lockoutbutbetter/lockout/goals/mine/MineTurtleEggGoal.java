package me.marin.lockoutbutbetter.lockout.goals.mine;

import me.marin.lockoutbutbetter.lockout.interfaces.MineBlockGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;
import java.util.Set;

public class MineTurtleEggGoal extends MineBlockGoal {

    private static final List<Item> ITEMS = List.of(Items.TURTLE_EGG);
    private static final Item ITEM = Items.TURTLE_EGG;

    public MineTurtleEggGoal(String id) {
        super(id);
    }

    @Override
    public String getGoalName() {
        return "Mine Turtle Egg";
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

