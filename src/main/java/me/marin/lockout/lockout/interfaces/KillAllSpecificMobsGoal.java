package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;

import java.util.LinkedHashSet;
import java.util.List;

public abstract class KillAllSpecificMobsGoal extends Goal implements Trackable<LockoutTeam, LinkedHashSet<EntityType<?>>>, HasTooltipInfo {

    public KillAllSpecificMobsGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<EntityType<?>> getEntityTypes();

}
