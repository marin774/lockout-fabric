package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class AngerZombifiedPiglinGoal extends Goal implements TextureProvider {

    public AngerZombifiedPiglinGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Enrage Zombified Piglin";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/enrage_zombie_piglin.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}