package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.component.type.PotionContentsComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Potion;
import net.minecraft.registry.entry.RegistryEntry;

public abstract class DrinkPotionGoal extends Goal {

    private final ItemStack displayItem;
    public DrinkPotionGoal(String id, String data) {
        super(id, data);
        displayItem = PotionContentsComponent.createStack(Items.POTION, getPotion());
    }

    @Override
    public ItemStack getTextureItemStack() {
        return displayItem;
    }

    public abstract RegistryEntry<Potion> getPotion();

}
