package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class FillCampfireWithFoodGoal extends Goal implements TextureProvider {

    public FillCampfireWithFoodGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Fill Campfire with 4 Food Items";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/campfire.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
}
