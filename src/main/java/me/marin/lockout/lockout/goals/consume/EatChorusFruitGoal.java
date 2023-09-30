package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EatChorusFruitGoal extends ConsumeItemGoal {

    public EatChorusFruitGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat Chorus Fruit";
    }

    @Override
    public Item getItem() {
        return Items.CHORUS_FRUIT;
    }

}
