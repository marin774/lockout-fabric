package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public abstract class KillSpecificMobsGoal extends Goal implements RequiresAmount, Trackable<LockoutTeam, Integer>, HasTooltipInfo {

    public KillSpecificMobsGoal(String id, String data) {
        super(id, data);
    }

    public abstract List<EntityType<?>> getEntityTypes();

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> lore = new ArrayList<>();

        lore.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            lore.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + getTrackerMap().getOrDefault(team, 0) + "/" + getAmount());
        }
        lore.add(" ");

        return lore;
    }

}
