package me.marin.lockout.lockout.goals.misc;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

public class TootGoatHornGoal extends Goal implements TextureProvider {

    private static final ItemStack ITEM_STACK = Items.GOAT_HORN.getDefaultStack();
    private static final Identifier TEXTURE = Identifier.of("minecraft", "textures/item/goat_horn.png");

    public TootGoatHornGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Toot a Goat Horn";
    }

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM_STACK;
    }

    @Override
    public Identifier getTextureIdentifier() {
        return TEXTURE;
    }
}
