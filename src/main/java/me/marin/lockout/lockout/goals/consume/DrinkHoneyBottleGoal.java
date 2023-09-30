package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class DrinkHoneyBottleGoal extends ConsumeItemGoal {

    public DrinkHoneyBottleGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Drink Honey Bottle";
    }

    @Override
    public Item getItem() {
        return Items.HONEY_BOTTLE;
    }

}
