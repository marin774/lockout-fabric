package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.entity.effect.StatusEffect;

public abstract class StatusEffectGoal extends Goal {

    public StatusEffectGoal(String id, String data) {
        super(id, data);
    }

    public abstract StatusEffect getStatusEffect();

}
