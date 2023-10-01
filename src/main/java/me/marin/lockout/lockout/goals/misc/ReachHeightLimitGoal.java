package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class ReachHeightLimitGoal extends Goal implements TextureProvider {

    public ReachHeightLimitGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Reach height limit in Overworld";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/height_limit.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}