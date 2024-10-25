package me.marin.lockout.lockout.goals.biome;

import me.marin.lockout.lockout.interfaces.VisitBiomeGoal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class VisitMushroomBiomeGoal extends VisitBiomeGoal {

    private static final ItemStack ITEM_STACK = Items.MYCELIUM.getDefaultStack();

    public VisitMushroomBiomeGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Find Mushroom Biome";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    private static final List<Identifier> BIOME_LIST = List.of(Identifier.of("minecraft", "mushroom_fields"));
    @Override
    public List<Identifier> getBiomes() {
        return BIOME_LIST;
    }

}
