package me.marin.lockout.lockout.goals.ride;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.RideEntityGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class RideHorseGoal extends RideEntityGoal {

    private static final ItemStack ITEM_STACK = Items.SADDLE.getDefaultStack();
    public RideHorseGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public EntityType<?> getEntityType() {
        return EntityType.HORSE;
    }

    @Override
    public String getGoalName() {
        return "Ride a Horse";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
