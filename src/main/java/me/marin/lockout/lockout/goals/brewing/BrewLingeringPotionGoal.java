package me.marin.lockout.lockout.goals.brewing;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class BrewLingeringPotionGoal extends Goal {

    private final ItemStack ITEM;
    public BrewLingeringPotionGoal(String id, String data) {
        super(id, data);
        ITEM = Items.LINGERING_POTION.getDefaultStack();
    }

    @Override
    public String getGoalName() {
        return "Brew a Lingering Potion";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM;
    }

}
