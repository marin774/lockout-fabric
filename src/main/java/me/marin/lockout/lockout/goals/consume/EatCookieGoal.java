package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import net.minecraft.item.Item;
import net.minecraft.item.Items;

public class EatCookieGoal extends ConsumeItemGoal {

    public EatCookieGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Eat Cookie";
    }

    @Override
    public Item getItem() {
        return Items.COOKIE;
    }

}
