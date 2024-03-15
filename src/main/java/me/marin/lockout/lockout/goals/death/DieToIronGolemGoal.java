package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.DieToEntityGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;

public class DieToIronGolemGoal extends DieToEntityGoal implements TextureProvider {

    public DieToIronGolemGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to Iron Golem";
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.IRON_GOLEM;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_golem.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
