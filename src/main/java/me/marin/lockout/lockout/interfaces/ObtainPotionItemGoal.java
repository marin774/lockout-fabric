package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.lockout.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PotionItem;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionUtil;

import java.util.List;

public abstract class ObtainPotionItemGoal extends Goal {

    private final ItemStack ITEM;
    public ObtainPotionItemGoal(String id, String data) {
        super(id, data);
        ItemStack item = Items.POTION.getDefaultStack();
        PotionUtil.setPotion(item, getPotions().get(0));
        ITEM = item;
    }

    public abstract List<Potion> getPotions();

    @Override
    public ItemStack getTextureItemStack() {
        return ITEM;
    }
}
