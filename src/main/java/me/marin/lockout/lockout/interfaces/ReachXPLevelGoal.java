package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.item.ItemStack;

public abstract class ReachXPLevelGoal extends Goal implements TextureProvider, RequiresAmount {

    public ReachXPLevelGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

}
