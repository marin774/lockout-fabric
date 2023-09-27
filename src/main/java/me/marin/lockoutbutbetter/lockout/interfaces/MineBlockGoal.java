package me.marin.lockoutbutbetter.lockout.interfaces;

import me.marin.lockoutbutbetter.lockout.Goal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.List;
import java.util.Set;

public abstract class MineBlockGoal extends Goal {

    public MineBlockGoal(String id) {
        super(id);
    }

    public abstract List<Item> getItems();

}
