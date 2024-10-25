package me.marin.lockout.lockout.goals.have_more;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class HaveMostXPLevelsGoal extends Goal implements TextureProvider {

    public HaveMostXPLevelsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Have the most XP Levels";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/more_level.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
