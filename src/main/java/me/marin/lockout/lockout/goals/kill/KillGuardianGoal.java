package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.lockout.interfaces.KillMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillGuardianGoal extends KillMobGoal {

    private static final Item ITEM = Items.GUARDIAN_SPAWN_EGG;

    public KillGuardianGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill a Guardian";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public EntityType<?> getEntity() {
        return EntityType.GUARDIAN;
    }
}
