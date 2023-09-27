package me.marin.lockoutbutbetter.server;

import me.marin.lockoutbutbetter.Constants;
import me.marin.lockoutbutbetter.Lockout;
import me.marin.lockoutbutbetter.client.LockoutBoard;
import me.marin.lockoutbutbetter.lockout.Goal;
import me.marin.lockoutbutbetter.lockout.GoalType;
import me.marin.lockoutbutbetter.lockout.interfaces.EnterDimensionGoal;
import me.marin.lockoutbutbetter.lockout.interfaces.KillMobGoal;
import me.marin.lockoutbutbetter.lockout.interfaces.ObtainItemsGoal;
import me.marin.lockoutbutbetter.mixin.server.PlayerInventoryAccessor;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityWorldChangeEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.Lock;

import static net.minecraft.server.command.CommandManager.literal;

public class LockoutButBetterServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var commandNode = literal("lockout").executes(context -> {
                PacketByteBuf buf = PacketByteBufs.create();
                List<String> goals = List.of(
                        GoalType.EAT_STEAK,
                        GoalType.OBTAIN_DIAMOND_TOOLS,
                        GoalType.MINE_DIAMOND_ORE,
                        GoalType.MINE_EMERALD_ORE,
                        GoalType.MINE_MOB_SPAWNER,
                        GoalType.MINE_TURTLE_EGG,
                        GoalType.ENTER_NETHER,
                        GoalType.ENTER_END,
                        GoalType.KILL_ENDER_DRAGON,
                        GoalType.OBTAIN_WOODEN_TOOLS,
                        GoalType.OBTAIN_GOLDEN_TOOLS,
                        GoalType.OBTAIN_STONE_TOOLS,
                        GoalType.OBTAIN_IRON_TOOLS,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL,
                        GoalType.NULL
                );
                for (String goal : goals) {
                    buf.writeString(goal);
                }

                Lockout lockout = new Lockout(new LockoutBoard(goals));

                ServerPlayNetworking.send((ServerPlayerEntity) context.getSource().getEntity(), Constants.BEGIN_LOCKOUT_PACKET, buf);
                context.getSource().getServer().getPlayerManager().broadcast(Text.literal("Lockout has begun."), false);

                return 1;
            }).build();

            dispatcher.getRoot().addChild(commandNode);
        });

        ServerEntityWorldChangeEvents.AFTER_PLAYER_CHANGE_WORLD.register((player, origin, destination) -> {
            if (!Lockout.isRunning()) return;

            Lockout lockout = Lockout.getInstance();

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (!(goal instanceof EnterDimensionGoal enterDimensionGoal)) continue;
                if (goal.isCompleted()) continue;

                if (destination.getDimensionKey().equals(enterDimensionGoal.getDimensionTypeKey())) {
                    lockout.completeGoal(goal, player);
                }
            }
        });

        ServerTickEvents.END_SERVER_TICK.register((server) -> {
            if (!Lockout.isRunning()) return;

            Lockout lockout = Lockout.getInstance();

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (goal.isCompleted()) continue;
                if (!(goal instanceof ObtainItemsGoal obtainItemsGoal)) continue;

                for (ServerPlayerEntity player : server.getPlayerManager().getPlayerList()) {
                    Set<Item> items = new HashSet<>(obtainItemsGoal.getItems());
                    out:
                    for (var defaultedList : ((PlayerInventoryAccessor) player.getInventory()).getCombinedInventory()) {
                        for (ItemStack item : defaultedList) {
                            if (item == null) continue;
                            if (items.remove(item.getItem())) {
                                if (items.isEmpty()) break out;
                            }
                        }
                    }
                    if (items.isEmpty()) {
                        lockout.completeGoal(goal, player);
                    }
                }
            }

        });

        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (!Lockout.isRunning()) return;
            entity.getServer().getPlayerManager().broadcast(Text.literal("Died to: " + source.getName()), false);

            if (source.isOf(DamageTypes.BAD_RESPAWN_POINT)) {
                entity.getServer().getPlayerManager().broadcast(Text.literal("died to bed or anchor"), false);
            }
            if (source.isOf(DamageTypes.EXPLOSION)) {
                entity.getServer().getPlayerManager().broadcast(Text.literal("died to explosion"), false);
            }
            if (source.isOf(DamageTypes.ARROW)) {
                entity.getServer().getPlayerManager().broadcast(Text.literal("died to arrow"), false);
            }
            if (!(source.getAttacker() instanceof PlayerEntity player)) return;
            entity.getServer().getPlayerManager().broadcast(Text.literal("killed by player"), false);

            Lockout lockout = Lockout.getInstance();

            for (Goal goal : lockout.getBoard().getGoals()) {
                if (goal == null) continue;
                if (!(goal instanceof KillMobGoal killMobGoal)) continue;
                if (goal.isCompleted()) continue;

                if (killMobGoal.getEntity().equals(entity.getType())) {
                    lockout.completeGoal(goal, player);
                }
            }
        });

    }

}
