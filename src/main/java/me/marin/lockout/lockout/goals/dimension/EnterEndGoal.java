package me.marin.lockout.lockout.goals.dimension;

import me.marin.lockout.lockout.interfaces.EnterDimensionGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;

public class EnterEndGoal extends EnterDimensionGoal {

    private static final Item ITEM = Items.END_PORTAL_FRAME;

    public EnterEndGoal(String id, String data) {
        super(id, data);
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
    public RegistryKey<World> getWorldRegistryKey() {
        return ServerWorld.END;
    }


}
