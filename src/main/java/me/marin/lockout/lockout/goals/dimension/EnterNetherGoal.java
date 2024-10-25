package me.marin.lockout.lockout.goals.dimension;

import me.marin.lockout.lockout.interfaces.EnterDimensionGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import static me.marin.lockout.Constants.NAMESPACE;

public class EnterNetherGoal extends EnterDimensionGoal implements TextureProvider {

    public EnterNetherGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Enter Nether";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

    @Override
    public RegistryKey<DimensionType> getDimensionTypeKey() {
        return DimensionTypes.THE_NETHER;
    }

    private static final Identifier TEXTURE = Identifier.of(NAMESPACE, "textures/custom/nether_portal.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }

}
