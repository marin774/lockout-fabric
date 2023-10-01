package me.marin.lockout.lockout.goals.workstation;

import me.marin.lockout.lockout.interfaces.IncrementStatGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.util.List;

public class UseEnchantingTableGoal extends IncrementStatGoal {

    public UseEnchantingTableGoal(String id, String data) {
        super(id, data);
    }

    private static final List<Identifier> STATS = List.of(Stats.ENCHANT_ITEM);
    @Override
    public List<Identifier> getStats() {
        return STATS;
    }

    @Override
    public String getGoalName() {
        return "Use Enchanting Table";
    }

    private static final ItemStack ITEM_STACK = Items.ENCHANTING_TABLE.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

}
