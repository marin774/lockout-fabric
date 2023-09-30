package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;

public abstract class EnterDimensionGoal extends Goal {

    public EnterDimensionGoal(String id, String data) {
        super(id, data);
    }

    public abstract RegistryKey<DimensionType> getDimensionTypeKey();

}
