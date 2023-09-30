package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EatSuspiciousStewGoal extends ConsumeItemGoal {

    public EatSuspiciousStewGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat Suspicious Stew";
    }

    @Override
    public Item getItem() {
        return Items.SUSPICIOUS_STEW;
    }

}
