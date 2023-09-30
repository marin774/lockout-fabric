package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import me.marin.lockout.lockout.interfaces.WearArmorPieceGoal;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class WearChainArmorPieceGoal extends WearArmorPieceGoal {

    private static final List<Item> ITEMS = List.of(Items.CHAINMAIL_HELMET, Items.CHAINMAIL_CHESTPLATE, Items.CHAINMAIL_LEGGINGS, Items.CHAINMAIL_BOOTS);

    public WearChainArmorPieceGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear a Chain Armor Piece";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        List<Item> items = new ArrayList<>(getItems());
        for (ItemStack item : playerInventory.armor) {
            if (item == null) continue;
            if (items.remove(item.getItem())) {
                return true;
            }
        }
        return false;
    }

}
