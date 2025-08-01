package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.mixin.server.PlayerInventoryAccessor;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class ObtainSomeOfTheItemsGoal extends ObtainItemsGoal implements RequiresAmount {

    public ObtainSomeOfTheItemsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        List<Item> items = new ArrayList<>(getItems());
        int count = 0;
        for (ItemStack item : ((PlayerInventoryAccessor) playerInventory).getPlayerInventory()) {
            if (item == null) continue;
            if (items.remove(item.getItem())) {
                count++;
                if (count >= getAmount()) {
                    return true;
                }
            }
        }
        for (var equipmentSlot : EquipmentSlot.values())
        {
            var item = ((PlayerInventoryAccessor) playerInventory).getEquipment().get(equipmentSlot);

            if (item == null) continue;
            if (items.remove(item.getItem())) {
                count++;
                if (count >= getAmount()) {
                    return true;
                }
            }
        }
        return false;
    }

}