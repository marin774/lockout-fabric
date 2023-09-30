package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EatRabbitStewGoal extends ConsumeItemGoal {

    public EatRabbitStewGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat Rabbit Stew";
    }

    @Override
    public Item getItem() {
        return Items.RABBIT_STEW;
    }

}
