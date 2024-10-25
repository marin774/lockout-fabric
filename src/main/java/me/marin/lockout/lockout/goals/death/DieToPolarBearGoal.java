package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.DieToEntityGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class DieToPolarBearGoal extends DieToEntityGoal implements TextureProvider {

    public DieToPolarBearGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to Polar Bear";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.POLAR_BEAR;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/death/die_to_polar_bear.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
