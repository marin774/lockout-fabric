package me.marin.lockout.lockout.goals.advancement;

import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import me.marin.lockout.lockout.texture.CycleItemTexturesProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.List;

public class GetHotTouristDestinationsAdvancementGoal extends AdvancementGoal implements CycleItemTexturesProvider {

    private static final ItemStack ITEM_STACK = Items.CRIMSON_NYLIUM.getDefaultStack();
    private static final List<Item> ITEMS_TO_DISPLAY = List.of(Items.CRIMSON_NYLIUM, Items.WARPED_NYLIUM, Items.SOUL_SOIL, Items.NETHERRACK, Items.BASALT);
    private static final List<Identifier> ADVANCEMENTS = List.of(new Identifier("minecraft", "nether/explore_nether"));

    public GetHotTouristDestinationsAdvancementGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Visit All Nether Biomes";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    @Override
    public List<Identifier> getAdvancements() {
        return ADVANCEMENTS;
    }

    @Override
    public List<Item> getItemsToDisplay() {
        return ITEMS_TO_DISPLAY;
    }
}
