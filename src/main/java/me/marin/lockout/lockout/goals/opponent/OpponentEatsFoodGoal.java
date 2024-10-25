package me.marin.lockout.lockout.goals.opponent;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class OpponentEatsFoodGoal extends Goal implements TextureProvider {

    public OpponentEatsFoodGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Opponent eats Food";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/opponent/no_food.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}