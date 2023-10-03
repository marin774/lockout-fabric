package me.marin.lockout.lockout.goals.advancement;

import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class GetWaxOnAdvancementGoal extends AdvancementGoal {

    private static final Item ITEM = Items.HONEYCOMB;

    public GetWaxOnAdvancementGoal(String id, String data) {
        super(id, data);
    }

    private static final List<Identifier> ADVANCEMENTS = List.of(new Identifier("minecraft", "husbandry/wax_on"));
    @Override
    public List<Identifier> getAdvancements() {
        return ADVANCEMENTS;
    }

    @Override
    public String getGoalName() {
        return "Apply Honeycomb to a Copper Block";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM.getDefaultStack();
    }

}
