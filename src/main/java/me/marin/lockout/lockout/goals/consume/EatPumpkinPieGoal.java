package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EatPumpkinPieGoal extends ConsumeItemGoal {

    public EatPumpkinPieGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat Pumpkin Pie";
    }

    @Override
    public Item getItem() {
        return Items.PUMPKIN_PIE;
    }

}
