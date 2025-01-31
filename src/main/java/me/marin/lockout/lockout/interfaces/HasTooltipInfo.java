package me.marin.lockout.lockout.interfaces;

import me.marin.lockout.LockoutTeam;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public interface HasTooltipInfo {

    List<String> getTooltip(LockoutTeam team);
    List<String> getSpectatorTooltip();

    int MAX_LINE_SIZE = 45;

    /**
     * Concatenates the values with commas, and splits them into multiple (shorter) lines.
     *
     * @param values list of strings to concatenate
     * @return concatenated and separated list
     */
    static List<String> commaSeparatedList(List<String> values) {
        List<String> lines = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {
            String val = values.get(i);
            boolean isLast = i + 1 == values.size();

            String[] words = val.split(" ");
            for (int j = 0; j < words.length; j++) {
                String word = words[j];
                boolean isLastWord = j + 1 == words.length;

                boolean canFit = sb.length() + word.length() + (isLastWord && !isLast ? 1 : 0) <= MAX_LINE_SIZE;

                if (!canFit) {
                    lines.add(Formatting.GRAY + " " + Formatting.ITALIC + sb.toString().trim());
                    sb.setLength(0);
                }

                sb.append(word);
                if (!isLast && isLastWord) {
                    sb.append(",");
                }
                sb.append(" ");
            }
        }

        if (!sb.isEmpty()) {
            lines.add(Formatting.GRAY + " " + Formatting.ITALIC + sb.toString().trim());
        }
        return lines;
    }

}
