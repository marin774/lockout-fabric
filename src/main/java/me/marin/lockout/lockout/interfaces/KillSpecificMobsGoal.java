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
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            tooltip.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + getTrackerMap().getOrDefault(team, 0) + "/" + getAmount());
        }
        tooltip.add(" ");

        return tooltip;
    }

}
