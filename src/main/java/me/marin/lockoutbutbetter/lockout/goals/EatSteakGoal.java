package me.marin.lockoutbutbetter.lockout.goals;

import me.marin.lockoutbutbetter.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EatSteakGoal extends ConsumeItemGoal {

    public EatSteakGoal(String id) {
        super(id);
    }

    @Override
    public String getGoalName() {
        return "Eat Steak";
    }

    @Override
    public Item getItem() {
        return Items.COOKED_BEEF;
    }

}
