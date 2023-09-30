package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;

public abstract class KillUniqueHostileMobsGoal extends Goal implements RequiresAmount {

    public KillUniqueHostileMobsGoal(String id, String data) {
        super(id, data);
    }

}
