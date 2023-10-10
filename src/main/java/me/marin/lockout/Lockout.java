package me.marin.lockout;

import me.marin.lockout.client.LockoutBoard;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.server.LockoutServer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

public class Lockout {

    private static final Logger logger = LogManager.getLogger("Lockout");
    public static final Random random = new Random();
    public final static int[] COLOR_ORDERS = new int[]{12, 9, 10, 14, 6, 13, 11, 5, 3, 2, 15, 4, 7, 1, 8, 0};

    public Map<LockoutTeam, LinkedHashSet<EntityType<?>>> bredAnimalTypes = new HashMap<>();
    public Map<LockoutTeam, LinkedHashSet<EntityType<?>>> killedHostileTypes = new HashMap<>();
    public Map<LockoutTeam, LinkedHashSet<EntityType<?>>> killedRaidMobs = new HashMap<>();
    public Map<LockoutTeam, Integer> killedUndeadMobs = new HashMap<>();
    public Map<LockoutTeam, Integer> killedArthropods = new HashMap<>();
    public Map<LockoutTeam, LinkedHashSet<FoodComponent>> foodTypesEaten = new HashMap<>();
    public Map<LockoutTeam, LinkedHashSet<Identifier>> uniqueAdvancements = new HashMap<>();
    public Map<UUID, Long> pumpkinWearStart = new HashMap<>();
    public Map<LockoutTeam, Double> damageTaken = new HashMap<>();
    public Map<LockoutTeam, Double> damageDealt = new HashMap<>();
    public Map<UUID, Integer> deaths = new HashMap<>();
    public Map<LockoutTeam, Integer> mobsKilled = new HashMap<>();
    // public Map<UUID, Integer> most_x_Item = new LinkedHashMap<>();
    public Map<UUID, Integer> distanceSprinted = new HashMap<>();
    public Map<UUID, Set<Item>> uniqueCrafts = new HashMap<>();

    public UUID mostUniqueCraftsPlayer;
    public int mostUniqueCrafts;

    private final LockoutBoard board;
    private final List<? extends LockoutTeam> teams;
    private long startTime;
    private long endTime;
    private boolean hasStarted = false;
    private boolean isRunning = true;

    public Lockout(LockoutBoard board, List<? extends LockoutTeam> teams) {
        this.board = board;
        this.teams = teams;
    }

    public LockoutBoard getBoard() {
        return board;
    }

    public String getModeName() {
        return teams.size() > 1 ? "Lockout" : "Blackout";
    }

    public boolean isSoloBlackout() {
        return teams.size() == 1 && teams.get(0).getPlayerNames().size() == 1;
    }

    public static void log(String message) {
        logger.log(Level.INFO, message);
    }

    public static boolean exists(Lockout lockout) {
        return lockout != null;
    }

    public static boolean isLockoutRunning(Lockout lockout) {
        return exists(lockout) && lockout.isRunning;
    }

    public void opponentCompletedGoal(Goal goal, PlayerEntity player, String message) {
        opponentCompletedGoal(goal, player.getUuid(), message);
    }

    public void opponentCompletedGoal(Goal goal, UUID playerId, String message) {
        if (goal.isCompleted()) return;
        if (!isLockoutPlayer(playerId)) return;
        if (!hasStarted()) return;

        LockoutTeamServer team = (LockoutTeamServer) getPlayerTeam(playerId);

        opponentCompletedGoal(goal, team, message);
    }
    public void opponentCompletedGoal(Goal goal, LockoutTeam team, String message) {
        if (goal.isCompleted()) return;
        if (!hasStarted()) return;

        LockoutTeamServer opponentTeam = (LockoutTeamServer) getOpponentTeam(team);
        opponentTeam.addPoint();

        ((LockoutTeamServer) team).sendMessage(Formatting.RED + message);
        opponentTeam.sendMessage(Formatting.GREEN + message);

        sendGoalCompletedPacket(goal, team);
        evaulateWinnerAndEndGame(team);
    }

    public void completeGoal(Goal goal, PlayerEntity player) {
        completeGoal(goal, player.getUuid());
    }
    public void completeGoal(Goal goal, UUID playerId) {
        if (goal.isCompleted()) return;
        if (!isLockoutPlayer(playerId)) return;
        if (!hasStarted()) return;

        LockoutTeamServer team = (LockoutTeamServer) getPlayerTeam(playerId);

        completeGoal(goal, team, team.getPlayerName(playerId) + " completed " + goal.getGoalName() + ".");
    }
    public void completeGoal(Goal goal, LockoutTeam team) {
        completeGoal(goal, team, team.getDisplayName() + " completed " + goal.getGoalName() + ".");
    }
    public void completeGoal(Goal goal, LockoutTeam team, String message) {
        if (goal.isCompleted()) return;
        if (!hasStarted()) return;

        team.addPoint();
        goal.setCompleted(true, team);

        for (LockoutTeam lockoutTeam : teams) {
            if (!(lockoutTeam instanceof LockoutTeamServer lockoutTeamServer)) continue;
            if (Objects.equals(lockoutTeamServer, team)) {
                lockoutTeamServer.sendMessage(Formatting.GREEN + message);
            } else {
                lockoutTeamServer.sendMessage(Formatting.RED + message);
            }
        }

        sendGoalCompletedPacket(goal, team);
        evaulateWinnerAndEndGame(team);
    }

    public void updateGoalCompletion(Goal goal, UUID playerId) {
        if (goal.isCompleted()) {
            clearGoalCompletion(goal, false);
        }
        completeGoal(goal, playerId);
    }

    public void clearGoalCompletion(Goal goal, boolean sendPacket) {
        if (!goal.isCompleted()) return;

        goal.getCompletedTeam().takePoint();
        goal.setCompleted(false, null);

        if (sendPacket) {
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeString(goal.getId());
            buf.writeInt(-1);
            for (ServerPlayerEntity serverPlayer : LockoutServer.server.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(serverPlayer, Constants.COMPLETE_TASK_PACKET, buf);
            }
        }
    }

    private void sendGoalCompletedPacket(Goal goal, LockoutTeam team) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeString(goal.getId());
        buf.writeInt(teams.indexOf(team));

        for (ServerPlayerEntity serverPlayer : LockoutServer.server.getPlayerManager().getPlayerList()) {
            ServerPlayNetworking.send(serverPlayer, Constants.COMPLETE_TASK_PACKET, buf);
        }
    }

    private void evaulateWinnerAndEndGame(LockoutTeam team) {
        PlayerManager playerManager = LockoutServer.server.getPlayerManager();

        List<LockoutTeam> winners = new ArrayList<>();
        if (isWinner(team)) {
            playerManager.broadcast(Text.literal(team.getDisplayName() + " wins."), false);
            winners.add(team);
            setRunning(false);
        } else {
            if (getRemainingGoals() == 0 && teams.size() > 1) {
                int maxCompleted = teams.stream().max(Comparator.comparingInt(LockoutTeam::getPoints)).get().getPoints();
                List<? extends LockoutTeam> winnerTeams = teams.stream().filter(t -> t.getPoints() == maxCompleted).toList();
                winners.addAll(winnerTeams);
                playerManager.broadcast(Text.literal("It's a tie! " + getWinnerTeamsString(winnerTeams) + " win."), false);
                setRunning(false);
            }
        }

        if (!this.isRunning) {
            PacketByteBuf bufEnd = PacketByteBufs.create();
            bufEnd.writeInt(winners.size());
            for (LockoutTeam winner : winners) {
                bufEnd.writeInt(teams.indexOf(winner));
            }
            bufEnd.writeLong(System.currentTimeMillis());

            for (ServerPlayerEntity serverPlayer : LockoutServer.server.getPlayerManager().getPlayerList()) {
                ServerPlayNetworking.send(serverPlayer, Constants.END_LOCKOUT_PACKET, bufEnd);
            }
        }
    }

    public boolean hasStarted() {
        return hasStarted;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
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

    public boolean isLockoutPlayer(UUID playerId) {
        for (LockoutTeam team : teams) {
            if (((LockoutTeamServer)team).getPlayers().contains(playerId)) {
                return true;
            }
        }
        return false;
    }

    public LockoutTeam getPlayerTeam(UUID playerId) {
        for (LockoutTeam team : teams) {
            if (((LockoutTeamServer)team).getPlayers().contains(playerId)) {
                return team;
            }
        }
        return null;
    }

    public LockoutTeam getOpponentTeam(UUID playerId) {
        for (LockoutTeam team : teams) {
            if (!((LockoutTeamServer)team).getPlayers().contains(playerId)) {
                return team;
            }
        }
        return null;
    }
    public LockoutTeam getOpponentTeam(LockoutTeam team) {
        for (LockoutTeam t : teams) {
            if (!Objects.equals(t, team)) {
                return team;
            }
        }
        return null;
    }

    public boolean isWinner(LockoutTeam team) {
        if (teams.size() == 1) {
            return getRemainingGoals() == 0;
        }
        for (LockoutTeam teamIt : teams) {
            if (team == teamIt) continue;
            if (teamIt.getPoints() + getRemainingGoals() >= team.getPoints()) {
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
            sb.append(team.getDisplayName());
        }
        return sb.toString();
    }

    public PacketByteBuf getStartTimePacket() {
        PacketByteBuf buf = PacketByteBufs.create();

        buf.writeLong(startTime);
        buf.writeLong(System.currentTimeMillis());

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

        buf.writeBoolean(this.isRunning);

        return buf;
    }


    public Map<UUID, Integer> levels = new LinkedHashMap<>();
    private UUID mostLevelsPlayer;

    public void recalculateXPGoal(Goal goal) {
        List<UUID> largestLevelPlayers = new ArrayList<>();
        int largestLevel = 0;

        for (UUID uuid : levels.keySet()) {
            if (levels.get(uuid) == largestLevel) {
                largestLevelPlayers.add(uuid);
                continue;
            }
            if (levels.get(uuid) > largestLevel) {
                largestLevelPlayers.clear();
                largestLevelPlayers.add(uuid);
                largestLevel = levels.get(uuid);
            }
        }

        if (largestLevel == 0) {
            if (this.mostLevelsPlayer != null) {
                this.mostLevelsPlayer = null;
                clearGoalCompletion(goal, true);
            }
            return;
        }

        if (!largestLevelPlayers.contains(mostLevelsPlayer)) {
            this.mostLevelsPlayer = largestLevelPlayers.get(0);
            updateGoalCompletion(goal, largestLevelPlayers.get(0));
        }
    }

}
