package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.DieToDamageTypeGoal;
import me.marin.lockout.lockout.texture.CycleTexturesProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.List;

public class DieToIntentionalGameDesignGoal extends DieToDamageTypeGoal implements CycleTexturesProvider {

    public DieToIntentionalGameDesignGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to [Intentional Game Design]";
    }

    @Override
    public List<RegistryKey<DamageType>> getDamageRegistryKeys() {
        return List.of(DamageTypes.BAD_RESPAWN_POINT);
    }

    @Override
    public Identifier getTextureIdentifier() {
        return null;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        return CycleTexturesProvider.super.renderTexture(context, x, y, tick);
    }
    private static final List<Identifier> TEXTURES = List.of(
            Identifier.of(Constants.NAMESPACE, "textures/custom/death/die_to_anchor.png"),
            Identifier.of(Constants.NAMESPACE, "textures/custom/death/die_to_bed.png")
    );

    @Override
    public List<Identifier> getTexturesToDisplay() {
        return TEXTURES;
    }

}
