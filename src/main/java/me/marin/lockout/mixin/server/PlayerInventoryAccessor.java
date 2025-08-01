package me.marin.lockout.mixin.server;

import net.minecraft.entity.EntityEquipment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerInventory.class)
public interface PlayerInventoryAccessor {

    @Accessor("main")
    DefaultedList<ItemStack> getPlayerInventory();

    @Accessor
    EntityEquipment getEquipment();

}
