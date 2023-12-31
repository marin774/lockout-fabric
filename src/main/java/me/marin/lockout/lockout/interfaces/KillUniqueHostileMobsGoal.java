package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.server.LockoutServer;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public abstract class KillUniqueHostileMobsGoal extends Goal implements RequiresAmount, HasTooltipInfo {

    public KillUniqueHostileMobsGoal(String id, String data) {
        super(id, data);
    }

    @Override
    public List<String> getTooltip(LockoutTeam team) {
        List<String> lore = new ArrayList<>();
        var hostiles = LockoutServer.lockout.killedHostileTypes.getOrDefault(team, new LinkedHashSet<>());

        lore.add(" ");
        lore.add("Unique Hostile Mobs Killed: " + LockoutServer.lockout.killedHostileTypes.getOrDefault(team, new LinkedHashSet<>()).size() + "/" + getAmount());
        lore.addAll(HasTooltipInfo.commaSeparatedList(hostiles.stream().map(type -> type.getName().getString()).toList()));
        lore.add(" ");

        return lore;
    }
}
