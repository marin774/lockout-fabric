package me.marin.lockout.lockout.goals.advancement;

import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class GetWhatADealAdvancementGoal extends AdvancementGoal {

    private static final ItemStack ITEM_STACK = Items.EMERALD.getDefaultStack();
    private static final List<Identifier> ADVANCEMENTS = List.of(Identifier.of("minecraft", "adventure/trade"));

    public GetWhatADealAdvancementGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Trade with a Villager";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    @Override
    public List<Identifier> getAdvancements() {
        return ADVANCEMENTS;
    }
}
