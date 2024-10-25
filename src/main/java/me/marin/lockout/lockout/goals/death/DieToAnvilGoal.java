package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.DieToDamageTypeGoal;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class DieToAnvilGoal extends DieToDamageTypeGoal {

    public DieToAnvilGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to falling Anvil";
    }

    @Override
    public List<RegistryKey<DamageType>> getDamageRegistryKeys() {
        return List.of(DamageTypes.FALLING_ANVIL);
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/death/die_to_anvil.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
