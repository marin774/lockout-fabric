package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class EmptyHungerBarGoal extends Goal implements TextureProvider {

    public EmptyHungerBarGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Empty Hunger Bar";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/starve.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}