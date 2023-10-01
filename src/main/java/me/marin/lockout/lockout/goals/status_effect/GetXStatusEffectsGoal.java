package me.marin.lockout.lockout.goals.status_effect;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.RequiresAmount;
import me.marin.lockout.lockout.texture.TextureProvider;

public abstract class GetXStatusEffectsGoal extends Goal implements RequiresAmount, TextureProvider {

    public GetXStatusEffectsGoal(String id, String data) {
        super(id, data);
    }

}
