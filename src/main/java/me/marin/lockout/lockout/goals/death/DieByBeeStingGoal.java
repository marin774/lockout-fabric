package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.DieToDamageTypeGoal;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;

public class DieByBeeStingGoal extends DieToDamageTypeGoal {

    public DieByBeeStingGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die by Bee Sting";
    }

    @Override
    public RegistryKey<DamageType> getDamageRegistryKey() {
        return DamageTypes.STING;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_bee.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
