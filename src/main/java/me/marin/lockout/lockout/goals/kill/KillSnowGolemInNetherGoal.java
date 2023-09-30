package me.marin.lockout.lockout.goals.kill;

import me.marin.lockout.lockout.interfaces.KillMobGoal;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class KillSnowGolemInNetherGoal extends KillSnowGolemGoal {

    public KillSnowGolemInNetherGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Kill a Snow Golem in The Nether";
    }

}
