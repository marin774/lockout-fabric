package me.marin.lockout.client;

import me.marin.lockout.Constants;
import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.client.gui.BoardScreen;
import me.marin.lockout.client.gui.BoardScreenHandler;
import me.marin.lockout.lockout.Goal;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public class LockoutClient implements ClientModInitializer {

    private static KeyBinding keyBinding;
    public static final ScreenHandlerType<BoardScreenHandler> BOARD_SCREEN_HANDLER;
    public static int CURRENT_TICK = 0;

    static {
        BOARD_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(Constants.NAMESPACE, "board"), BoardScreenHandler::new);
    }

    @Override
    public void onInitializeClient() {

        ClientPlayNetworking.registerGlobalReceiver(Constants.LOCKOUT_GOALS_TEAMS_PACKET, (client, handler, buf, responseSender) -> {
            if (client.player == null) {
                return;
            }
            client.execute(() -> {
                int teamsSize = buf.readInt();
                List<LockoutTeam> teams = new ArrayList<>();
                for (int i = 0; i < teamsSize; i++) {
                    int teamSize = buf.readInt();
                    Formatting color = Formatting.byName(buf.readString());
                    List<String> playerNames = new ArrayList<>();
                    for (int j = 0; j < teamSize; j++) {
                        playerNames.add(buf.readString());
                    }
                    teams.add(new LockoutTeam(playerNames, color));
                }

                List<Pair<String, String>> goals = new ArrayList<>();
                List<Integer> completedByTeam = new ArrayList<>();
                for (int i = 0; i < 25; i++) {
                    goals.add(new Pair<>(buf.readString(), buf.readString()));
                    completedByTeam.add(buf.readInt()); // index of team that completed the goal, -1 otherwise
                }

                for (LockoutTeam team : teams) {
                    client.player.sendMessage(Text.of(team.getColor() + team.getDisplayName()));
                    for (String playerName : team.getPlayerNames()) {
                        client.player.sendMessage(Text.of("  " + playerName));
                    }
                }
                Lockout lockout = new Lockout(new LockoutBoard(goals), teams);

                List<Goal> goalList = lockout.getBoard().getGoals();
                for (int i = 0; i < goalList.size(); i++) {
                    if (completedByTeam.get(i) != -1) {
                        goalList.get(i).setCompleted(true, lockout.getTeams().get(completedByTeam.get(i)));
                    }
                }

                MinecraftClient.getInstance().setScreen(new BoardScreen(BOARD_SCREEN_HANDLER.create(0, client.player.getInventory()), client.player.getInventory(), Text.empty()));
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.START_LOCKOUT_PACKET, (client, handler, buf, responseSender) -> {
            if (client.player == null) {
                return;
            }
            client.execute(() -> {
                Lockout.getInstance().setStarted(true);
                Lockout.getInstance().setStartTime(buf.readLong());
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.COMPLETE_TASK_PACKET, (client, handler, buf, responseSender) -> {
            if (client.player == null) {
                return;
            }
            client.execute(() -> {
                String goalId = buf.readString();
                int teamIndex = buf.readInt();
                Lockout.getInstance().getTeams().get(teamIndex).addPoint();
                Lockout.getInstance().getBoard().getGoals().stream().filter(goal -> goal.getId().equals(goalId)).findFirst().get().setCompleted(true, Lockout.getInstance().getTeams().get(teamIndex));
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 2f, 1f);
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.END_LOCKOUT_PACKET, (client, handler, buf, responseSender) -> {
            if (client.player == null) {
                return;
            }
            client.execute(() -> {
                Lockout.getInstance().setRunning(false);
                client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 2f, 1f);
            });
        });

        keyBinding = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.lockout.open_board", // The translation key of the keybinding's name
                InputUtil.Type.KEYSYM, // The type of the keybinding, KEYSYM for keyboard, MOUSE for mouse.
                GLFW.GLFW_KEY_B, // The keycode of the key
                "category.lockout.keybinds" // The translation key of the keybinding's category.
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            CURRENT_TICK++;

            boolean wasPressed = false;
            while (keyBinding.wasPressed()) {
                wasPressed = true;
            }
            if (wasPressed) {

                if (!Lockout.isLockoutRunning() || MinecraftClient.getInstance().currentScreen != null) {
                    return;
                }

                // Open GUI
                MinecraftClient.getInstance().setScreen(new BoardScreen(BOARD_SCREEN_HANDLER.create(0, client.player.getInventory()), client.player.getInventory(), Text.literal("Test")));
            }
        });

        HandledScreens.register(BOARD_SCREEN_HANDLER, BoardScreen::new);

    }

}
