package me.marin.lockout;

import lombok.Getter;
import net.minecraft.util.Formatting;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class LockoutTeam {

    private final List<String> players;
    @Getter
    private final Formatting color;
    @Getter
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

    public void addPoint() {
        this.points++;
    }
    public void takePoint() {
        this.points--;
    }

    public static String formattingToString(Formatting formatting) {
        return StringUtils.capitalize(formatting.asString().replace("_", " "));
    }

}
