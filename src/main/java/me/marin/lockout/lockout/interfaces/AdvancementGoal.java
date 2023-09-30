package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.util.Identifier;

import java.util.List;

public abstract class AdvancementGoal extends Goal {

    public AdvancementGoal(String id, String data) {
        super(id, data);
    }

    /**
     * Only one has to be obtained!
     */
    public abstract List<Identifier> getAdvancements();

}
