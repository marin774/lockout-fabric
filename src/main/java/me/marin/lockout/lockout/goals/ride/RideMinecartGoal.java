package me.marin.lockout.lockout.goals.ride;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.RideEntityGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RideMinecartGoal extends RideEntityGoal {

    private static final ItemStack ITEM_STACK = Items.MINECART.getDefaultStack();
    public RideMinecartGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public EntityType<?> getEntityType() {
        return EntityType.MINECART;
    }

    @Override
    public String getGoalName() {
        return "Ride a Minecart";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
