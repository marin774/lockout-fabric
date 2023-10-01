package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public abstract class GetUniqueAdvancementsGoal extends Goal implements RequiresAmount, Trackable<UUID, Set<Identifier>>, TextureProvider {

    private final ItemStack DISPLAY_ITEM_STACK = Items.APPLE.getDefaultStack();

    public GetUniqueAdvancementsGoal(String id, String data) {
        super(id, data);
        DISPLAY_ITEM_STACK.setCount(getAmount());
    }

    @Override
    public ItemStack getTextureItemStack() {
        return DISPLAY_ITEM_STACK;
    }

    @Override
    public Map<UUID, Set<Identifier>> getTrackerMap() {
        return Lockout.getInstance().uniqueAdvancementsMap;
    }

}
