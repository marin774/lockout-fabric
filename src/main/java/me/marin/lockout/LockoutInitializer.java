package me.marin.lockout;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import me.marin.lockout.lockout.DefaultGoalRegister;
import me.marin.lockout.server.LockoutServer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.loot.v2.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.command.argument.GameProfileArgumentType;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.Items;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.SetCountLootFunction;
import net.minecraft.loot.function.SetNbtLootFunction;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;

import java.util.Objects;
import java.util.function.Predicate;

public class LockoutInitializer implements ModInitializer {

    private static final Predicate<ServerCommandSource> PERMISSIONS = (ssc) -> ssc.hasPermissionLevel(2) || ssc.getServer().isSingleplayer();

    @Override
    public void onInitialize() {
        DefaultGoalRegister.registerGoals();

        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            {
                {
                    // Lockout command
                    var commandNode = CommandManager.literal("lockout").requires(PERMISSIONS).build();
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
                    var commandNode = CommandManager.literal("blackout").requires(PERMISSIONS).build();
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
                var giveGoalRoot = CommandManager.literal("GiveGoal").requires(PERMISSIONS).build();
                var playerName = CommandManager.argument("player name", GameProfileArgumentType.gameProfile()).build();
                var goalIndex = CommandManager.argument("goal number", IntegerArgumentType.integer(1, 25)).executes(LockoutServer::giveGoal).build();

                dispatcher.getRoot().addChild(giveGoalRoot);
                giveGoalRoot.addChild(playerName);
                playerName.addChild(goalIndex);
            }

            {
                // SetStartTime command
                var setStartTimeRoot = CommandManager.literal("SetStartTime").requires(PERMISSIONS).build();
                var seconds = CommandManager.argument("seconds", IntegerArgumentType.integer(5, 300)).executes(LockoutServer::setStartTime).build();

                dispatcher.getRoot().addChild(setStartTimeRoot);
                setStartTimeRoot.addChild(seconds);
            }

            {
                // RemoveCustomBoard command (SetCustomBoard is registered LockoutClient, and server listens for a packet)

                dispatcher.getRoot().addChild(CommandManager.literal("RemoveCustomBoard").requires(PERMISSIONS).executes((context) -> {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBoolean(true); // whether board should be cleared
                    ClientPlayNetworking.send(Constants.CUSTOM_BOARD_PACKET, buf);
                    return 1;
                }).build());
            }

        });

        LootTableEvents.REPLACE.register(((resourceManager, lootManager, id, original, source) -> {
            if (Objects.equals(id, LootTables.PIGLIN_BARTERING_GAMEPLAY)) {
                NbtCompound fireRes = new NbtCompound();
                fireRes.putString("Potion", "minecraft:fire_resistance");

                UniformLootNumberProvider ironNuggetsCount = UniformLootNumberProvider.create(9.0F, 36.0F);
                UniformLootNumberProvider quartzCount = UniformLootNumberProvider.create(8.0F, 16.0F);
                UniformLootNumberProvider glowstoneDustCount = UniformLootNumberProvider.create(5.0F, 12.0F);
                UniformLootNumberProvider magmaCreamCount = UniformLootNumberProvider.create(2.0F, 6.0F);
                UniformLootNumberProvider enderPearlCount = UniformLootNumberProvider.create(4.0F, 8.0F);
                UniformLootNumberProvider stringCount = UniformLootNumberProvider.create(8.0F, 24.0F);
                UniformLootNumberProvider fireChargeCount = UniformLootNumberProvider.create(1.0F, 5.0F);
                UniformLootNumberProvider gravelCount = UniformLootNumberProvider.create(8.0F, 16.0F);
                UniformLootNumberProvider leatherCount = UniformLootNumberProvider.create(4.0F, 10.0F);
                UniformLootNumberProvider netherBrickCount = UniformLootNumberProvider.create(4.0F, 16.0F);
                UniformLootNumberProvider cryingObsidianCount = UniformLootNumberProvider.create(1.0F, 3.0F);
                UniformLootNumberProvider soulSandCount = UniformLootNumberProvider.create(4.0F, 16.0F);

                LootPool pool = LootPool.builder()
                        .with(ItemEntry.builder(Items.BOOK).apply(EnchantRandomlyLootFunction.create().add(Enchantments.SOUL_SPEED)).weight(5))
                        .with(ItemEntry.builder(Items.IRON_BOOTS).apply(EnchantRandomlyLootFunction.create().add(Enchantments.SOUL_SPEED)).weight(8))
                        .with(ItemEntry.builder(Items.POTION).apply(SetNbtLootFunction.builder(fireRes)).weight(10))
                        .with(ItemEntry.builder(Items.SPLASH_POTION).apply(SetNbtLootFunction.builder(fireRes)).weight(10))
                        .with(ItemEntry.builder(Items.IRON_NUGGET).apply(SetCountLootFunction.builder(ironNuggetsCount)).weight(10))
                        .with(ItemEntry.builder(Items.QUARTZ).apply(SetCountLootFunction.builder(quartzCount)).weight(20))
                        .with(ItemEntry.builder(Items.GLOWSTONE_DUST).apply(SetCountLootFunction.builder(glowstoneDustCount)).weight(20))
                        .with(ItemEntry.builder(Items.MAGMA_CREAM).apply(SetCountLootFunction.builder(magmaCreamCount)).weight(20))
                        .with(ItemEntry.builder(Items.ENDER_PEARL).apply(SetCountLootFunction.builder(enderPearlCount)).weight(20))
                        .with(ItemEntry.builder(Items.STRING).apply(SetCountLootFunction.builder(stringCount)).weight(20))
                        .with(ItemEntry.builder(Items.FIRE_CHARGE).apply(SetCountLootFunction.builder(fireChargeCount)).weight(40))
                        .with(ItemEntry.builder(Items.GRAVEL).apply(SetCountLootFunction.builder(gravelCount)).weight(40))
                        .with(ItemEntry.builder(Items.LEATHER).apply(SetCountLootFunction.builder(leatherCount)).weight(40))
                        .with(ItemEntry.builder(Items.NETHER_BRICK).apply(SetCountLootFunction.builder(netherBrickCount)).weight(40))
                        .with(ItemEntry.builder(Items.OBSIDIAN).weight(40))
                        .with(ItemEntry.builder(Items.CRYING_OBSIDIAN).apply(SetCountLootFunction.builder(cryingObsidianCount)).weight(40))
                        .with(ItemEntry.builder(Items.SOUL_SAND).apply(SetCountLootFunction.builder(soulSandCount)).weight(40))
                        .build();
                return LootTable.builder().pool(pool).build();
            }
            return null;
        }));

    }

}
