package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public interface HasTooltipInfo {

    List<String> getTooltip(LockoutTeam team);
    List<String> getSpectatorTooltip();

    int MAX_LINE_SIZE = 40;
    static List<String> commaSeparatedList(List<String> values) {
        List<String> lines = new ArrayList<>();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < values.size(); i++) {
            String part = values.get(i);
            boolean isLast = i + 1 == values.size();

            sb.append(part);
            if (!isLast) {
                sb.append(", ");
            }

            if (sb.length() + part.length() + (isLast ? 0 : 2) > MAX_LINE_SIZE) {
                lines.add(Formatting.GRAY + " " + Formatting.ITALIC + sb);
                sb = new StringBuilder();
            }
        }
        if (!sb.isEmpty()) {
            lines.add(Formatting.GRAY + " " + Formatting.ITALIC + sb);
        }
        return lines;
    }

}
