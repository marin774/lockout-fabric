package me.marin.lockout;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class LockoutConfig {

    private static final Path CONFIG_PATH = new File("./config/lockout.json").toPath();

    @Getter
    private static LockoutConfig instance;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    @SerializedName("default board size")
    public int boardSize = 5;

    @SerializedName("board side")
    public BoardSide boardSide = BoardSide.RIGHT;

    public static void load() {
        if (!Files.exists(CONFIG_PATH)) {
            loadDefaultConfig();
            save();
        } else {
            try {
                String s = Files.readString(CONFIG_PATH);
                instance = GSON.fromJson(s, LockoutConfig.class);
            } catch (Exception e) {
                Lockout.log("Invalid config file, using default values.");
                loadDefaultConfig();
            }
        }
    }

    public static void loadDefaultConfig() {
        instance = new LockoutConfig();
        instance.boardSize = 5;
        instance.boardSide = BoardSide.RIGHT;
    }

    public static void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(instance));
        } catch (Exception e) {
            Lockout.error(e);
        }
    }

    public enum BoardSide {
        @SerializedName("left")
        LEFT,
        @SerializedName("right")
        RIGHT;

        public static BoardSide match(String boardSide) {
            return switch (boardSide) {
                case "left" -> LEFT;
                case "right" -> RIGHT;
                default -> null;
            };
        }
    }


}
