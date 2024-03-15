package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.CycleTexturesProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

import java.util.List;

public class DieToFallingOffVinesGoal extends Goal implements CycleTexturesProvider {

    public DieToFallingOffVinesGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die by falling off Vines";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final List<Identifier> TEXTURES = List.of(
            new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_vines.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_twisting_vines.png"),
            new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_weeping_vines.png")
    );

    @Override
    public List<Identifier> getTexturesToDisplay() {
        return TEXTURES;
    }
}
