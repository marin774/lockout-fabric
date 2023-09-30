package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.Item;

import java.util.List;

public abstract class MineBlockGoal extends Goal {

    public MineBlockGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<Item> getItems();

}
