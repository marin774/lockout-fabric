package me.marin.lockout.lockout.goals.obtain;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;


public class PlaceEndCrystalGoal extends Goal implements TextureProvider {

    public PlaceEndCrystalGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Place End Crystal";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/end_crystal.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

    @Override
    public boolean renderTexture(DrawContext context, int x, int y, int tick) {
        return TextureProvider.super.renderTexture(context, x, y, tick);
    }
}
