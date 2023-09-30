package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class VisitBiomeGoal extends Goal {

    public VisitBiomeGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<Identifier> getBiomes();

}
