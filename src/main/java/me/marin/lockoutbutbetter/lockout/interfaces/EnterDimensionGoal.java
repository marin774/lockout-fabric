package me.marin.lockoutbutbetter.lockout.interfaces;

import me.marin.lockoutbutbetter.lockout.Goal;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

public abstract class EnterDimensionGoal extends Goal {

    public EnterDimensionGoal(String id) {
        super(id);
    }

    public abstract RegistryKey<DimensionType> getDimensionTypeKey();

}
