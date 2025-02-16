package me.marin.lockout.lockout.goals.ride;

import me.marin.lockout.lockout.interfaces.RideEntityGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RidePigGoal extends RideEntityGoal {

    private static final ItemStack ITEM_STACK = Items.CARROT_ON_A_STICK.getDefaultStack();
    public RidePigGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public EntityType<?> getEntityType() {
        return EntityType.PIG;
    }

    @Override
    public String getGoalName() {
        return "Ride Pig with Carrot on a Stick";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
