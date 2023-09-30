package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;

public abstract class RideEntityGoal extends Goal {

    public RideEntityGoal(String id, String data) {
        super(id, data);
    }

    public abstract EntityType<?> getEntityType();

}
