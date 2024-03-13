package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.PotionUtil;
import net.minecraft.potion.Potions;

public class DrinkWaterBottleGoal extends ConsumeItemGoal {

    public DrinkWaterBottleGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Drink Water Bottle";
    }

    private static final ItemStack ITEM_STACK;
    static {
        ITEM_STACK = Items.POTION.getDefaultStack();
        PotionUtil.setPotion(ITEM_STACK, Potions.WATER);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    @Override
    public Item getItem() {
        return Items.POTION;
    }

}
