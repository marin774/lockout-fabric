package me.marin.lockoutbutbetter.lockout.goals.dimension;

import me.marin.lockoutbutbetter.lockout.interfaces.EnterDimensionGoal;
import me.marin.lockoutbutbetter.lockout.texture.CustomTextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import static me.marin.lockoutbutbetter.Constants.NETHER_PORTAL_TEXTURE;

public class EnterNetherGoal extends EnterDimensionGoal implements CustomTextureProvider {

    public EnterNetherGoal(String id) {
        super(id);
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

    @Override
    public void renderTexture(DrawContext context, int x, int y, int tick) {
        context.drawTexture(NETHER_PORTAL_TEXTURE, x, y, 0, 0, 16, 16, 16, 16);
    }

}
