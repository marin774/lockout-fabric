package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.lockout.interfaces.KillMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillElderGuardianGoal extends KillMobGoal {

    private static final Item ITEM = Items.ELDER_GUARDIAN_SPAWN_EGG;

    public KillElderGuardianGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill an Elder Guardian";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public EntityType<?> getEntity() {
        return EntityType.ELDER_GUARDIAN;
    }
}
