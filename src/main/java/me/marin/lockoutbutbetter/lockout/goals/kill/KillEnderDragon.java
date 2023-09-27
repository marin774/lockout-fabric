package me.marin.lockoutbutbetter.lockout.goals.kill;

import me.marin.lockoutbutbetter.lockout.interfaces.KillMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillEnderDragon extends KillMobGoal {

    private static final Item ITEM = Items.DRAGON_EGG;

    public KillEnderDragon(String id) {
        super(id);
    }

    @Override
    public String getGoalName() {
        return "Kill Ender Dragon";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

    @Override
    public EntityType<?> getEntity() {
        return EntityType.ENDER_DRAGON;
    }
}
