package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.DieToDamageTypeGoal;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class DieByFallingStalactiteGoal extends DieToDamageTypeGoal {

    public DieByFallingStalactiteGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to Falling Stalactite";
    }

    @Override
    public RegistryKey<DamageType> getDamageRegistryKey() {
        return DamageTypes.FALLING_STALACTITE;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_falling_stalactite.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
