package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.DrinkPotionGoal;
import net.minecraft.potion.Potion;
import net.minecraft.potion.Potions;

public class DrinkWaterBottleGoal extends DrinkPotionGoal {

    public DrinkWaterBottleGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public Potion getPotion() {
        return Potions.WATER;
    }

    @Override
    public String getGoalName() {
        return "Drink Water Bottle";
    }



}
