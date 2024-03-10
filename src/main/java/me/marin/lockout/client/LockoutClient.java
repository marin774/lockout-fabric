package me.marin.lockout.client;

import me.marin.lockout.Constants;
import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.client.gui.BoardBuilderIO;
import me.marin.lockout.client.gui.BoardScreen;
import me.marin.lockout.client.gui.BoardScreenHandler;
import me.marin.lockout.json.JSONBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LockoutClient implements ClientModInitializer {

    public static Lockout lockout;
    public static boolean amIPlayingLockout = false;
    private static KeyBinding keyBinding;
    public static int CURRENT_TICK = 0;
    public static final Map<String, String> goalLoreMap = new HashMap<>();

    public static boolean shouldOpenBoardBuilder = false;

    public static final ScreenHandlerType<BoardScreenHandler> BOARD_SCREEN_HANDLER;

    static {
        BOARD_SCREEN_HANDLER = ScreenHandlerRegistry.registerSimple(Constants.BOARD_SCREEN_ID, BoardScreenHandler::new);
    }

    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(Constants.LOCKOUT_GOALS_TEAMS_PACKET, (client, handler, buf, responseSender) -> {
            int teamsSize = buf.readInt();
            List<LockoutTeam> teams = new ArrayList<>();

            boolean amIPlaying = false;

            for (int i = 0; i < teamsSize; i++) {
                int teamSize = buf.readInt();
                Formatting color = Formatting.byName(buf.readString());
                List<String> playerNames = new ArrayList<>();
                for (int j = 0; j < teamSize; j++) {
                    String playerName = buf.readString();
                    playerNames.add(playerName);
                    amIPlaying |= playerName.equals(client.player.getName().getString());
                }
                teams.add(new LockoutTeam(playerNames, color));
            }
            LockoutClient.amIPlayingLockout = amIPlaying;

            List<Pair<String, String>> goals = new ArrayList<>();
            List<Integer> completedByTeam = new ArrayList<>();
            for (int i = 0; i < 25; i++) {
                goals.add(new Pair<>(buf.readString(), buf.readString()));
                completedByTeam.add(buf.readInt()); // index of team that completed the goal, -1 otherwise
            }

            lockout = new Lockout(new LockoutBoard(goals), teams);
            lockout.setRunning(buf.readBoolean());

            List<Goal> goalList = lockout.getBoard().getGoals();
            for (int i = 0; i < goalList.size(); i++) {
                if (completedByTeam.get(i) != -1) {
                    LockoutTeam team = lockout.getTeams().get(completedByTeam.get(i));
                    goalList.get(i).setCompleted(true, team);
                    team.addPoint();
                }
            }

            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new BoardScreen(BOARD_SCREEN_HANDLER.create(0, client.player.getInventory()), client.player.getInventory(), Text.empty()));
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.UPDATE_LORE, (client, handler, buf, responseSender) -> {
            goalLoreMap.put(buf.readString(), buf.readString());
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.START_LOCKOUT_PACKET, (client, handler, buf, responseSender) -> {
            lockout.setStarted(true);
            client.execute(() -> {
                if (MinecraftClient.getInstance().currentScreen != null) {
                    MinecraftClient.getInstance().currentScreen.close();
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.UPDATE_TIMER_PACKET, (client, handler, buf, responseSender) -> {
            lockout.setTicks(buf.readLong());
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.COMPLETE_TASK_PACKET, (client, handler, buf, responseSender) -> {
            String goalId = buf.readString();
            int teamIndex = buf.readInt();

            client.execute(() -> {
                Goal goal = lockout.getBoard().getGoals().stream().filter(g -> g.getId().equals(goalId)).findFirst().get();
                if (goal.isCompleted() || teamIndex == -1) {
                    lockout.clearGoalCompletion(goal, false);
                }
                if (teamIndex != -1) {
                    LockoutTeam team = lockout.getTeams().get(teamIndex);
                    team.addPoint();
                    goal.setCompleted(true, lockout.getTeams().get(teamIndex));

                    if (client.player != null && amIPlayingLockout) {
                        if (team.getPlayerNames().contains(client.player.getName().getString())) {
                            client.player.playSound(SoundEvents.BLOCK_NOTE_BLOCK_CHIME.value(), 2f, 1f);
                        } else {
                            client.player.playSound(SoundEvents.ENTITY_GUARDIAN_DEATH, 2f, 1f);
                        }
                    }
                }


            });
        });
        ClientPlayNetworking.registerGlobalReceiver(Constants.END_LOCKOUT_PACKET, (client, handler, buf, responseSender) -> {
            int winnerTeamsSize = buf.readInt();
            List<Integer> winners = new ArrayList<>();
            for (int i = 0; i < winnerTeamsSize; i++) {
                winners.add(buf.readInt());
            }

            lockout.setRunning(false);
            buf.readLong();
            client.execute(() -> {
                if (client.player != null) {
                    boolean didIWin = false;
                    for (Integer winner : winners) {
                        LockoutTeam team = lockout.getTeams().get(winner);

                        if (team.getPlayerNames().contains(client.player.getName().getString())) {
                            didIWin = true;
                            break;
                        }
                    }
                    if (didIWin) {
                        client.player.playSound(SoundEvents.ENTITY_PILLAGER_CELEBRATE, 2f, 1f);
                    } else {
                        client.player.playSound(SoundEvents.ENTITY_WARDEN_DEATH, 2f, 1f);
                    }
                }
            });
        });

        ArgumentTypeRegistry.registerArgumentType(Constants.BOARD_FILE_ARGUMENT_TYPE, CustomBoardFileArgumentType.class, ConstantArgumentSerializer.of(CustomBoardFileArgumentType::newInstance));

        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            {
                var commandNode = ClientCommandManager.literal("BoardBuilder").executes((context) -> {
                    shouldOpenBoardBuilder = true;
                    return 1;
                }).build();

                dispatcher.getRoot().addChild(commandNode);
            }
            {
                var commandNode = ClientCommandManager.literal("SetCustomBoard").requires(ccs -> {
                    if (MinecraftClient.getInstance().isInSingleplayer()) {
                        return true;
                    }
                    return ccs.hasPermissionLevel(2);
                }).build();

                var boardNameNode = ClientCommandManager.argument("board name", CustomBoardFileArgumentType.newInstance()).executes((context) -> {
                    String boardName = context.getArgument("board name", String.class);

                    JSONBoard jsonBoard;
                    try {
                        jsonBoard = BoardBuilderIO.INSTANCE.readBoard(boardName);
                    } catch (IOException e) {
                        context.getSource().sendError(Text.literal("Error while trying to read board."));
                        return 0;
                    }

                    if (jsonBoard.goals.size() != 25) {
                        context.getSource().sendError(Text.literal("Board doesn't have 25 goals!"));
                        return 0;
                    }

                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(false); // whether board should be cleared
                    for (JSONBoard.JSONGoal goal : jsonBoard.goals) {
                        buf.writeString(goal.id);
                        buf.writeString(goal.data == null ? GoalDataConstants.DATA_NONE : goal.data);
                    }

                    ClientPlayNetworking.send(Constants.CUSTOM_BOARD_PACKET, buf);
                    return 1;
                }).build();

                commandNode.addChild(boardNameNode);
                dispatcher.getRoot().addChild(commandNode);
            }
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

                if (!Lockout.exists(lockout) || client.currentScreen != null || client.player == null) {
                    return;
                }

                // Open GUI
                client.setScreen(new BoardScreen(BOARD_SCREEN_HANDLER.create(0, client.player.getInventory()), client.player.getInventory(), Text.empty()));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            lockout = null;
            goalLoreMap.clear();
        }));

        HandledScreens.register(BOARD_SCREEN_HANDLER, BoardScreen::new);

    }

}
