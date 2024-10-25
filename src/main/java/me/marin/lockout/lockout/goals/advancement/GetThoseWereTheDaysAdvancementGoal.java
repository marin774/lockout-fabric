package me.marin.lockout.lockout.goals.advancement;

import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class GetThoseWereTheDaysAdvancementGoal extends AdvancementGoal {

    private static final ItemStack ITEM_STACK = Items.POLISHED_BLACKSTONE_BRICKS.getDefaultStack();
    private static final List<Identifier> ADVANCEMENTS = List.of(Identifier.of("minecraft", "nether/find_bastion"));

    public GetThoseWereTheDaysAdvancementGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Find a Bastion";
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
