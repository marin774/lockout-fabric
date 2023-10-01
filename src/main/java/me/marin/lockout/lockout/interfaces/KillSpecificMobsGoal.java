package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;

import java.util.List;
import java.util.UUID;

public abstract class KillSpecificMobsGoal extends Goal implements RequiresAmount, Trackable<UUID, Integer> {

    public KillSpecificMobsGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<EntityType<?>> getEntityTypes();

}
