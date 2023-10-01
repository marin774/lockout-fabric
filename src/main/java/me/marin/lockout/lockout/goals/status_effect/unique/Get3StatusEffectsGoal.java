package me.marin.lockout.lockout.goals.status_effect.unique;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.goals.status_effect.GetXStatusEffectsGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Get3StatusEffectsGoal extends GetXStatusEffectsGoal {

    public Get3StatusEffectsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Get 3 Status Effects at once";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    @Override
    public int getAmount() {
        return 3;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/status_effect/3_status_effects.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
