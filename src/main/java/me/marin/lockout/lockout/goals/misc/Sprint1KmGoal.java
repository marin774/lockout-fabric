package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Sprint1KmGoal extends Goal implements TextureProvider {

    public Sprint1KmGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Sprint 1km";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/sprint_1km.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}