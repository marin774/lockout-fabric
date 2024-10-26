package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

import java.util.List;

public abstract class ObtainPotionItemGoal extends Goal {

    private final ItemStack ITEM;
    public ObtainPotionItemGoal(String id, String data) {
        super(id, data);
        ITEM = PotionContentsComponent.createStack(Items.POTION, getPotions().getFirst());
    }

    public abstract List<RegistryEntry<Potion>> getPotions();

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM;
    }
}
