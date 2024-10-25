package me.marin.lockout.lockout.goals.workstation;

import me.marin.lockout.Constants;
import me.marin.lockout.lockout.interfaces.IncrementStatGoal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.stat.Stats;
import net.minecraft.util.Identifier;

import java.util.List;

public class UseCauldronGoal extends IncrementStatGoal implements TextureProvider {

    public UseCauldronGoal(String id, String data) {
        super(id, data);
    }

    private static final List<Identifier> STATS = List.of(Stats.CLEAN_ARMOR, Stats.CLEAN_BANNER, Stats.CLEAN_SHULKER_BOX);
    @Override
    public List<Identifier> getStats() {
        return STATS;
    }

    @Override
    public String getGoalName() {
        return "Use a Cauldron to wash something";
    }

    private static final ItemStack ITEM_STACK = Items.ENCHANTING_TABLE.getDefaultStack();
    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    private static final Identifier TEXTURE = Identifier.of(Constants.NAMESPACE, "textures/custom/use_cauldron.png");
    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
}
