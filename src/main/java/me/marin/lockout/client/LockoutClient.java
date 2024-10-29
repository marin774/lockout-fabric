package me.marin.lockout.client;

import me.marin.lockout.Constants;
import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeam;
import me.marin.lockout.client.gui.BoardBuilderIO;
import me.marin.lockout.client.gui.BoardBuilderScreen;
import me.marin.lockout.client.gui.BoardScreen;
import me.marin.lockout.client.gui.BoardScreenHandler;
import me.marin.lockout.json.JSONBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import me.marin.lockout.network.*;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import oshi.util.tuples.Pair;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.marin.lockout.Constants.MAX_BOARD_SIZE;
import static me.marin.lockout.Constants.MIN_BOARD_SIZE;

public class LockoutClient implements ClientModInitializer {

    public static Lockout lockout;
    public static boolean amIPlayingLockout = false;
    private static KeyBinding keyBinding;
    public static int CURRENT_TICK = 0;
    public static final Map<String, String> goalTooltipMap = new HashMap<>();

    public static boolean shouldOpenBoardBuilder = false;

    public static final ScreenHandlerType<BoardScreenHandler> BOARD_SCREEN_HANDLER;

    static {
        BOARD_SCREEN_HANDLER = new ScreenHandlerType<>(BoardScreenHandler::new, FeatureFlags.VANILLA_FEATURES);
    }

    @Override
    public void onInitializeClient() {
        Registry.register(Registries.SCREEN_HANDLER, Constants.BOARD_SCREEN_ID, BOARD_SCREEN_HANDLER);

        ClientPlayNetworking.registerGlobalReceiver(LockoutGoalsTeamsPayload.ID, (payload, context) -> {
            List<LockoutTeam> teams = payload.teams();

            LockoutClient.amIPlayingLockout = teams.stream().map(LockoutTeam::getPlayerNames)
                    .anyMatch(players -> players.stream().anyMatch(player -> player.equals(MinecraftClient.getInstance().getSession().getUsername())));

            int[] completedByTeam = payload.goals().stream().mapToInt(Pair::getB).toArray();

            lockout = new Lockout(new LockoutBoard(payload.goals().stream().map(Pair::getA).toList()), teams);
            lockout.setRunning(payload.isRunning());

            List<Goal> goalList = lockout.getBoard().getGoals();
            for (int i = 0; i < goalList.size(); i++) {
                if (completedByTeam[i] != -1) {
                    LockoutTeam team = lockout.getTeams().get(completedByTeam[i]);
                    goalList.get(i).setCompleted(true, team);
                    team.addPoint();
                }
            }

            MinecraftClient client = context.client();
            client.execute(() -> {
                if (client.player != null) {
                    client.setScreen(new BoardScreen(BOARD_SCREEN_HANDLER.create(0, client.player.getInventory()), client.player.getInventory(), Text.empty()));
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(UpdateTooltipPayload.ID, (payload, context) -> {
            goalTooltipMap.put(payload.goal(), payload.tooltip());
        });
        ClientPlayNetworking.registerGlobalReceiver(StartLockoutPayload.ID, (payload, context) -> {
            Lockout.log("STARTED");
            lockout.setStarted(true);
            context.client().execute(() -> {
                if (MinecraftClient.getInstance().currentScreen != null) {
                    MinecraftClient.getInstance().currentScreen.close();
                }
            });
        });
        ClientPlayNetworking.registerGlobalReceiver(UpdateTimerPayload.ID, (payload, context) -> {
            lockout.setTicks(payload.ticks());
        });
        ClientPlayNetworking.registerGlobalReceiver(CompleteTaskPayload.ID, (payload, context) -> {
            MinecraftClient client = context.client();
            client.execute(() -> {
                Goal goal = lockout.getBoard().getGoals().stream().filter(g -> g.getId().equals(payload.goal())).findFirst().get();
                if (goal.isCompleted() || payload.teamIndex() == -1) {
                    lockout.clearGoalCompletion(goal, false);
                }
                if (payload.teamIndex() != -1) {
                    LockoutTeam team = lockout.getTeams().get(payload.teamIndex());
                    team.addPoint();
                    goal.setCompleted(true, lockout.getTeams().get(payload.teamIndex()));

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
        ClientPlayNetworking.registerGlobalReceiver(EndLockoutPayload.ID, (payload, context) -> {
            lockout.setRunning(false);
            MinecraftClient client = context.client();
            client.execute(() -> {
                if (client.player != null) {
                    boolean didIWin = false;
                    for (int winner : payload.winners()) {
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

                    int size = (int) Math.sqrt(jsonBoard.goals.size());
                    if (size * size != jsonBoard.goals.size() || size < MIN_BOARD_SIZE || size > MAX_BOARD_SIZE) {
                        context.getSource().sendError(Text.literal("Board doesn't have a valid number of goals!"));
                        return 0;
                    }

                    ClientPlayNetworking.send(new CustomBoardPayload(Optional.of(jsonBoard.goals.stream()
                            .map(goal -> new Pair<>(goal.id, goal.data != null ? goal.data : GoalDataConstants.DATA_NONE)).toList())));
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
                if (client.currentScreen != null || client.player == null) {
                    return;
                }

                // If the game hasn't started, open board builder instead
                if (!Lockout.exists(lockout)) {
                    client.setScreen(new BoardBuilderScreen());
                    return;
                }

                // Open GUI
                client.setScreen(new BoardScreen(BOARD_SCREEN_HANDLER.create(0, client.player.getInventory()), client.player.getInventory(), Text.empty()));
            }
        });
        ClientPlayConnectionEvents.DISCONNECT.register(((handler, client) -> {
            lockout = null;
            goalTooltipMap.clear();
        }));

        HandledScreens.register(BOARD_SCREEN_HANDLER, BoardScreen::new);

    }

}
