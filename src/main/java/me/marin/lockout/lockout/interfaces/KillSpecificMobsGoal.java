package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.EntityType;

import java.util.List;

public abstract class KillSpecificMobsGoal extends Goal implements RequiresAmount, Trackable<LockoutTeam, Integer>, HasTooltipInfo {

    public KillSpecificMobsGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<EntityType<?>> getEntityTypes();

}
