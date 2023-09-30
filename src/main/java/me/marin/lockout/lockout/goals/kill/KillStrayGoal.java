package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.lockout.interfaces.KillMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillStrayGoal extends KillMobGoal {

    private static final Item ITEM = Items.STRAY_SPAWN_EGG;

    public KillStrayGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill a Stray";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public EntityType<?> getEntity() {
        return EntityType.STRAY;
    }
}
