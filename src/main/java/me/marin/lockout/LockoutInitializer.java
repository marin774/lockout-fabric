package me.marin.lockout;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.marin.lockout.lockout.DefaultGoalRegister;
import me.marin.lockout.server.LockoutServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;

public class LockoutInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        DefaultGoalRegister.registerGoals();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            {
                {
                    // Lockout command
                    var commandNode = CommandManager.literal("lockout").build();
                    var teamsNode = CommandManager.literal("teams").build();
                    var playersNode = CommandManager.literal("players").build();
                    var teamListNode = CommandManager.argument("team names", StringArgumentType.greedyString()).executes(LockoutServer::lockoutCommandLogic).build();
                    var playerListNode = CommandManager.argument("player names", StringArgumentType.greedyString()).executes(LockoutServer::lockoutCommandLogic).build();

                    dispatcher.getRoot().addChild(commandNode);
                    commandNode.addChild(teamsNode);
                    commandNode.addChild(playersNode);
                    teamsNode.addChild(teamListNode);
                    playersNode.addChild(playerListNode);
                }


                {
                    // Blackout command
                    var commandNode = CommandManager.literal("blackout").build();
                    var teamNode = CommandManager.literal("team").build();
                    var playersNode = CommandManager.literal("players").build();
                    var teamNameNode = CommandManager.argument("team name", StringArgumentType.greedyString()).executes(LockoutServer::blackoutCommandLogic).build();
                    var playerListNode = CommandManager.argument("player names", StringArgumentType.greedyString()).executes(LockoutServer::blackoutCommandLogic).build();

                    dispatcher.getRoot().addChild(commandNode);
                    commandNode.addChild(teamNode);
                    commandNode.addChild(playersNode);
                    teamNode.addChild(teamNameNode);
                    playersNode.addChild(playerListNode);
                }
            }


            {
                // Chat command
                var chatCommandNode = CommandManager.literal("chat").build();
                var chatTeamNode = CommandManager.literal("team").executes(context -> LockoutServer.setChat(context, ChatManager.Type.TEAM)).build();
                var chatLocalNode = CommandManager.literal("local").executes(context -> LockoutServer.setChat(context, ChatManager.Type.LOCAL)).build();

                dispatcher.getRoot().addChild(chatCommandNode);
                chatCommandNode.addChild(chatTeamNode);
                chatCommandNode.addChild(chatLocalNode);
            }


            {
                // GiveGoal command
                var giveGoalRoot = CommandManager.literal("GiveGoal").build();
                var playerName = CommandManager.argument("player name", GameProfileArgumentType.gameProfile()).build();
                var goalIndex = CommandManager.argument("goal number", IntegerArgumentType.integer(1, 25)).executes(LockoutServer::giveGoal).build();

                dispatcher.getRoot().addChild(giveGoalRoot);
                giveGoalRoot.addChild(playerName);
                playerName.addChild(goalIndex);
            }

            {
                // RemoveCustomBoard command (SetCustomBoard is registered LockoutClient, and server listens for a packet)

                dispatcher.getRoot().addChild(CommandManager.literal("RemoveCustomBoard").requires(ssc -> ssc.hasPermissionLevel(2)).executes((context) -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(true); // whether board should be cleared
                    ClientPlayNetworking.send(Constants.CUSTOM_BOARD_PACKET, buf);
                    return 1;
                }).build());
            }

        });

    }

}
