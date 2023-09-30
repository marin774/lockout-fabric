package me.marin.lockout.lockout.goals.consume;

import me.marin.lockout.lockout.interfaces.IncrementStatGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.util.List;

public class EatCakeGoal extends IncrementStatGoal {

    public EatCakeGoal(String id, String data) {
        super(id, data);
    }

    private static final List<Identifier> STATS = List.of(Stats.EAT_CAKE_SLICE);
    @Override
    public List<Identifier> getStats() {
        return STATS;
    }

    @Override
    public String getGoalName() {
        return "Eat a slice of Cake";
    }

    private static final ItemStack ITEM_STACK = Items.CAKE.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
