package me.marin.lockout;

import net.minecraft.util.Formatting;
import org.apache.commons.lang3.text.WordUtils;

import java.util.List;

public class LockoutTeam {

    private final List<String> players;
    private final Formatting color;
    private int points = 0;

    public LockoutTeam(List<String> playerNames, Formatting formattingColor) {
        this.players = playerNames;
        this.color = formattingColor;
    }

    public List<String> getPlayerNames() {
        return players;
    }

    public String getDisplayName() {
        return players.size() == 1 ? players.get(0) : "Team " + formattingToString(color);
    }

    public Formatting getColor() {
        return color;
    }

    public int getPoints() {
        return points;
    }

    public void addPoint() {
        this.points++;
    }
    public void takePoint() {
        this.points--;
    }

    public static String formattingToString(Formatting formatting) {
        return WordUtils.capitalize(formatting.asString().replace("_", " "));
    }

}
