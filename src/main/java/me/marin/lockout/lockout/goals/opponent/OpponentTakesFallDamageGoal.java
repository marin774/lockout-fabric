package me.marin.lockout.lockout.goals.opponent;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class OpponentTakesFallDamageGoal extends Goal implements TextureProvider {

    public OpponentTakesFallDamageGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Opponent takes Fall Damage";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/opponent/no_fall_damage.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}