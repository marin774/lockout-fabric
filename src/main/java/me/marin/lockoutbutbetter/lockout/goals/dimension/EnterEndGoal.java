package me.marin.lockoutbutbetter.lockout.goals.dimension;

import me.marin.lockoutbutbetter.lockout.interfaces.EnterDimensionGoal;
import me.marin.lockoutbutbetter.lockout.texture.CustomTextureProvider;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.DimensionTypes;

import static me.marin.lockoutbutbetter.Constants.NETHER_PORTAL_TEXTURE;

public class EnterEndGoal extends EnterDimensionGoal {

    private static final Item ITEM = Items.END_PORTAL_FRAME;

    public EnterEndGoal(String id) {
        super(id);
    }

    @Override
    public String getGoalName() {
        return "Enter The End";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public RegistryKey<DimensionType> getDimensionTypeKey() {
        return DimensionTypes.THE_END;
    }


}
