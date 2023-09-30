package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.texture.TextureProvider;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;

public abstract class DieToDamageTypeGoal extends Goal implements TextureProvider {

    public DieToDamageTypeGoal(String id, String data) {
        super(id, data);
    }

    public abstract RegistryKey<DamageType> getDamageRegistryKey();

    @Override
    public ItemStack getTextureItemStack() {
        return null;
    }

}
