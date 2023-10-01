package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.interfaces.WearArmorPieceGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

import java.util.List;

public class WearCarvedPumpkinFor5MinutesGoal extends WearArmorPieceGoal {

    private static final List<Item> ITEMS = List.of(Items.CARVED_PUMPKIN);

    public WearCarvedPumpkinFor5MinutesGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public String getGoalName() {
        return "Wear a Carved Pumpkin for 5 minutes";
    }

    @Override
    public List<Item> getItems() {
        return ITEMS;
    }

    @Override
    public boolean satisfiedBy(PlayerInventory playerInventory) {
        PlayerEntity player = playerInventory.player;
        var map = Lockout.getInstance().pumpkinWearStart;

        Long startTime = map.get(player.getUuid());

        boolean wearingPumpkin = false;
        for (ItemStack item : playerInventory.armor) {
            if (item == null) continue;
            if (item.getItem() == Items.CARVED_PUMPKIN) {
                wearingPumpkin = true;
                break;
            }
        }

        if (wearingPumpkin) {
            if (startTime == null) {
                map.put(player.getUuid(), System.currentTimeMillis());
                return false;
            } else {
                return System.currentTimeMillis() - startTime >= (1000 * 60 * 5);
            }
        } else {
            map.remove(player.getUuid());
        }

        return false;
    }


}
