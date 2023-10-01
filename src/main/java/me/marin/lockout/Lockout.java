package me.marin.lockout;

import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.util.GoalDataConstants;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Lockout {

    private static final Logger logger = LogManager.getLogger("Lockout");
    public static final Random random = new Random();
    public final static int[] COLOR_ORDERS = new int[]{12, 9, 10, 14, 6, 13, 11, 5, 3, 2, 15, 4, 7, 1, 8, 0};

    public Map<PlayerEntity, Set<EntityType<?>>> bredAnimalTypes = new HashMap<>();
    public Map<PlayerEntity, Set<EntityType<?>>> killedHostileTypes = new HashMap<>();
    public Map<PlayerEntity, Integer> killedUndeadMobs = new HashMap<>();
    public Map<PlayerEntity, Integer> killedArthropods = new HashMap<>();
    public Map<PlayerEntity, Set<FoodComponent>> foodTypesEaten = new HashMap<>();
    public Map<PlayerEntity, Set<Identifier>> uniqueAdvancementsMap = new HashMap<>();
    public Map<PlayerEntity, Long> pumpkinWearStart = new HashMap<>();

    private static Lockout INSTANCE;
    private final LockoutBoard board;
    private final List<? extends LockoutTeam> teams;
    private long startTime;
    private boolean hasStarted = false;
    private boolean isRunning = true;

    public Lockout(LockoutBoard board, List<? extends LockoutTeam> teams) {
        INSTANCE = this;
        this.board = board;
        this.teams = teams;
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
    public static boolean exists() {
        return INSTANCE != null;
    };
    public static boolean isLockoutRunning() {
        return exists() && INSTANCE.isRunning;
    };

    public void completeGoal(Goal goal, PlayerEntity player) {
        if (goal.isCompleted()) return;
        if (!isLockoutPlayer(player)) {
            return;
        }

        LockoutTeam team = getPlayerTeam(player);
        goal.setCompleted(true, team);

        PlayerManager playerManager = player.getServer().getPlayerManager();
        team.addPoint();

        player.getServer().getPlayerManager().broadcast(Text.literal(player.getName().getString() + " completed " + goal.getGoalName() + "."), false);

        if (isWinner(team)) {
            setRunning(false);
        } else {
            if (getRemainingGoals() == 0 && teams.size() > 1) {
                int maxCompleted = teams.stream().max(Comparator.comparingInt(LockoutTeam::getPoints)).get().getPoints();
                List<? extends LockoutTeam> winnerTeams = teams.stream().filter(t -> t.getPoints() == maxCompleted).toList();
                playerManager.broadcast(Text.literal("It's a tie! Teams " + getWinnerTeamsString(winnerTeams) + " win."), false);
                setRunning(false);
            }
        }

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(goal.getId());
        buf.writeInt(teams.indexOf(team));

        for (ServerPlayerEntity serverPlayer : player.getServer().getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(serverPlayer, Constants.COMPLETE_TASK_PACKET, buf);
            if (!this.isRunning) {
                ServerPlayNetworking.send(serverPlayer, Constants.END_LOCKOUT_PACKET, PacketByteBufs.empty());
            }
        }
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public void setStarted(boolean hasStarted) {
        this.hasStarted = hasStarted;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public boolean isLockoutPlayer(PlayerEntity player) {
        if (player.getServer() != null) {
            for (LockoutTeam team : teams) {
                // If there's issues with logging on and off - replace this check with UUIDs.
                if (((LockoutTeamServer)team).getPlayers().contains((ServerPlayerEntity) player)) return true;
            }
            return false;
        }
        return true;
    }

    public LockoutTeam getPlayerTeam(PlayerEntity player) {
        for (LockoutTeam team : teams) {
            if (((LockoutTeamServer)team).getPlayers().contains((ServerPlayerEntity) player)) {
                return team;
            }
        }
        return null;
    }

    public boolean isWinner(LockoutTeam team) {
        for (LockoutTeam teamIt : teams) {
            if (team == teamIt) continue;
            if (teamIt.getPoints() + getRemainingGoals() >= team.getPoints()) {
                System.out.println("Not a winner: " + teamIt.getDisplayName() + " can still win with " + (teamIt.getPoints() + getRemainingGoals()) + " points (" + team.getDisplayName() + " has " + team.getPoints() + ")");
                return false;
            }
        }
        return true;
    }

    public int getRemainingGoals() {
        return (int) board.getGoals().stream().filter(goal -> !goal.isCompleted()).count();
    }

    public List<? extends LockoutTeam> getTeams() {
        return teams;
    }

    public void setRunning(boolean running) {
        isRunning = running;
    }



    private static String getWinnerTeamsString(List<? extends LockoutTeam> teams) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < teams.size(); i++) {
            if (i > 0) {
                if (i + 1 == teams.size()) {
                    sb.append(" and ");
                } else {
                    sb.append(", ");
                }
            }
            LockoutTeam team = teams.get(i);
            sb.append(team.getColor());
        }
        return sb.toString();
    }

    public PacketByteBuf getStartTimePacket() {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeLong(startTime);

        return buf;
    }

    public PacketByteBuf getTeamsGoalsPacket() {
        PacketByteBuf buf = PacketByteBufs.create();

        // Write teams
        buf.writeInt(teams.size());
        for (LockoutTeam team : teams) {
            buf.writeInt(team.getPlayerNames().size());
            buf.writeString(team.getColor().asString());
            for (String playerName : team.getPlayerNames()) {
                buf.writeString(playerName);
            }
        }

        // Write goals
        for (Goal goal : board.getGoals()) {
            buf.writeString(goal.getId());
            buf.writeString(goal.getData());
            buf.writeInt(goal.isCompleted() ? teams.indexOf(goal.getCompletedTeam()) : -1);
        }

        return buf;
    }

}
