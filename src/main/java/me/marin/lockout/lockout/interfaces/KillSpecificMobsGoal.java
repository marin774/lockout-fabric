package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;

import java.util.List;
import java.util.Map;

public abstract class KillSpecificMobsGoal extends Goal implements RequiresAmount, Trackable<PlayerEntity, Integer> {

    public KillSpecificMobsGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<EntityType<?>> getEntityTypes();

}
