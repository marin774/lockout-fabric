package me.marin.lockout.lockout.goals.death;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.DieToDamageTypeGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class DieByIronGolemGoal extends Goal implements TextureProvider {

    public DieByIronGolemGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Die to Iron Golem";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    private static final Identifier TEXTURE = new Identifier(Constants.NAMESPACE, "textures/custom/death/die_to_golem.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
