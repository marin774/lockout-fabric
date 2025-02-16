package me.marin.lockout.lockout.goals.wear_armor;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.Utility;
import me.marin.lockout.lockout.interfaces.HasTooltipInfo;
import me.marin.lockout.lockout.interfaces.WearArmorPieceGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class WearCarvedPumpkinFor5MinutesGoal extends WearArmorPieceGoal implements HasTooltipInfo {

    private static final List<Item> ITEMS = List.of(Items.CARVED_PUMPKIN);
    private static final int FIVE_MINUTES_TICKS = 20 * 60 * 5;

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
        var map = LockoutServer.lockout.pumpkinWearTime;

        long wornTime = map.getOrDefault(player.getUuid(), 0L);

        boolean wearingPumpkin = false;
        for (ItemStack item : playerInventory.armor) {
            if (item == null) continue;
            if (item.getItem() == Items.CARVED_PUMPKIN) {
                wearingPumpkin = true;
                break;
            }
        }

        if (wearingPumpkin) {
            wornTime += 1;
            map.put(player.getUuid(), wornTime);

            if (wornTime % 20 == 0) {
                ((LockoutTeamServer) LockoutServer.lockout.getPlayerTeam(player.getUuid())).sendTooltipUpdate(this, true);
            }

            return wornTime >= (FIVE_MINUTES_TICKS);
        }

        return false;
    }


    @Override
    public List<String> getTooltip(LockoutTeam team, PlayerEntity player) {
        List<String> tooltip = new ArrayList<>();
        long timeWorn = Math.min(FIVE_MINUTES_TICKS, LockoutServer.lockout.pumpkinWearTime.getOrDefault(player.getUuid(), 0L));
        LockoutTeamServer serverTeam = ((LockoutTeamServer) team);

        tooltip.add(" ");
        tooltip.add("Time worn: " + Utility.ticksToTimer(timeWorn));
        if (serverTeam.getPlayers().size() > 1) {
            tooltip.add(" ");
            for (UUID uuid : ((LockoutTeamServer) team).getPlayers()) {
                if (!Objects.equals(uuid, player.getUuid())) {
                    tooltip.add(serverTeam.getPlayerName(uuid) + ": " + Utility.ticksToTimer(timeWorn));
                }
            }
        }
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            for (UUID uuid : ((LockoutTeamServer) team).getPlayers()) {
                long timeWorn = Math.min(FIVE_MINUTES_TICKS, LockoutServer.lockout.pumpkinWearTime.getOrDefault(uuid, 0L));
                tooltip.add(team.getColor() + ((LockoutTeamServer) team).getPlayerName(uuid) + Formatting.RESET + ": " + Utility.ticksToTimer(timeWorn));
            }
        }
        tooltip.add(" ");

        return tooltip;
    }
}
