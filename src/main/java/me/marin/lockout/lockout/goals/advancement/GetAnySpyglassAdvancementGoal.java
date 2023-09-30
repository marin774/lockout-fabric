package me.marin.lockout.lockout.goals.advancement;

import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class GetAnySpyglassAdvancementGoal extends AdvancementGoal {

    private static final ItemStack ITEM_STACK = Items.SPYGLASS.getDefaultStack();
    private static final List<Identifier> ADVANCEMENTS = List.of(
            new Identifier("minecraft", "adventure/spyglass_at_parrot"),
            new Identifier("minecraft", "adventure/spyglass_at_ghast"),
            new Identifier("minecraft", "adventure/spyglass_at_dragon")
            );

    public GetAnySpyglassAdvancementGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Get any Spyglass Advancement";
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
