package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class KillUniqueHostileMobsGoal extends Goal implements RequiresAmount, HasTooltipInfo {

    public KillUniqueHostileMobsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> tooltip = new ArrayList<>();
        var hostiles = LockoutServer.lockout.killedHostileTypes.getOrDefault(team, new LinkedHashSet<>());

        tooltip.add(" ");
        tooltip.add("Unique Hostile Mobs Killed: " + LockoutServer.lockout.killedHostileTypes.getOrDefault(team, new LinkedHashSet<>()).size() + "/" + getAmount());
        tooltip.addAll(HasTooltipInfo.commaSeparatedList(hostiles.stream().map(type -> type.getName().getString()).toList()));
        tooltip.add(" ");

        return tooltip;
    }

    @Override
    public List<String> getSpectatorTooltip() {
        List<String> tooltip = new ArrayList<>();

        tooltip.add(" ");
        for (LockoutTeam team : LockoutServer.lockout.getTeams()) {
            var hostiles = LockoutServer.lockout.killedHostileTypes.getOrDefault(team, new LinkedHashSet<>());
            tooltip.add(team.getColor() + team.getDisplayName() + Formatting.RESET + ": " + hostiles.size() + "/" + getAmount());
        }
        tooltip.add(" ");

        return tooltip;
    }

}
