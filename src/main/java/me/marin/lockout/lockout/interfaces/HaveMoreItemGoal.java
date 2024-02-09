package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.Item;

/**
 * TODO
 */
public abstract class HaveMoreItemGoal extends Goal {

    public HaveMoreItemGoal(String id, String data) {
        super(id, data);
    }

    public abstract Item getItem();

}
