package me.marin.lockoutbutbetter.lockout.interfaces;

import me.marin.lockoutbutbetter.lockout.Goal;
import net.minecraft.entity.EntityType;

public abstract class KillMobGoal extends Goal {

    public KillMobGoal(String id) {
        super(id);
    }

    public abstract EntityType<?> getEntity();

}
