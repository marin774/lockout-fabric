package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.lockout.interfaces.WearArmorGoal;
import me.marin.lockout.lockout.interfaces.WearArmorPieceGoal;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.ArrayList;
import java.util.List;

public class WearCarvedPumpkinArmorPieceGoal extends WearArmorPieceGoal {

    private static final List<Item> ITEMS = List.of(Items.CARVED_PUMPKIN);

    public WearCarvedPumpkinArmorPieceGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear a Carved Pumpkin";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

}
