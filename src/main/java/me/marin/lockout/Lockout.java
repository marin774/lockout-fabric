package me.marin.lockout;

import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.lockout.Goal;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Lockout {

    private static final Logger logger = LogManager.getLogger("Lockout");

    private static Lockout INSTANCE;
    private final LockoutBoard board;

    public Map<PlayerEntity, Set<EntityType<?>>> bredAnimalTypes = new HashMap<>();
    public Map<PlayerEntity, Set<EntityType<?>>> killedHostileTypes = new HashMap<>();
    public Map<PlayerEntity, Integer> killedUndeadMobs = new HashMap<>();
    public Map<PlayerEntity, Integer> killedArthropods = new HashMap<>();
    public Map<PlayerEntity, Set<FoodComponent>> foodTypesEaten = new HashMap<>();

    public static PlayerEntity latestBedExplosion = null;

    public Lockout(LockoutBoard board) {
        INSTANCE = this;
        this.board = board;


    }

    public LockoutBoard getBoard() {
        return board;
    }

    public static void log(String message) {
        logger.log(Level.INFO, message);
    }

    public static Lockout getInstance() {
        return INSTANCE;
    };
    public static boolean isRunning() {
        return INSTANCE != null;
    }

    public void completeGoal(Goal goal, PlayerEntity player) {
        if (goal.isCompleted()) return;

        goal.setCompleted(true);
        player.getServer().getPlayerManager().broadcast(Text.literal(player.getName().getString() + " completed " + goal.getGoalName() + "."), false);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(goal.getId());

        for (ServerPlayerEntity serverPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(serverPlayer, Constants.COMPLETE_TASK_PACKET, buf);
        }
    }

}
