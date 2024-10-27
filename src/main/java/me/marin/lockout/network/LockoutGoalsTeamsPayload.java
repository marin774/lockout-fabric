package me.marin.lockout.network;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutTeam;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.util.Formatting;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;

public record LockoutGoalsTeamsPayload(List<LockoutTeam> teams, List<Pair<Pair<String, String>, Integer>> goals,
                                       boolean isRunning) implements CustomPayload {
    public static final CustomPayload.Id<LockoutGoalsTeamsPayload> ID = new CustomPayload.Id<>(Constants.LOCKOUT_GOALS_TEAMS_PACKET);

    public static final PacketCodec<RegistryByteBuf, LockoutGoalsTeamsPayload> CODEC = new PacketCodec<RegistryByteBuf, LockoutGoalsTeamsPayload>() {
        @Override
        public LockoutGoalsTeamsPayload decode(RegistryByteBuf buf) {
            // Read teams
            int teamsSize = buf.readInt();
            List<LockoutTeam> teams = new ArrayList<>(teamsSize);
            for (int i = 0; i < teamsSize; i++) {
                int teamSize = buf.readInt();
                Formatting color = Formatting.byName(buf.readString());
                List<String> playerNames = new ArrayList<>();
                for (int j = 0; j < teamSize; j++) {
                    String playerName = buf.readString();
                    playerNames.add(playerName);
                }
                teams.add(new LockoutTeam(playerNames, color));
            }

            // Read goals
            int size = buf.readInt();
            List<Pair<Pair<String, String>, Integer>> goals = new ArrayList<>(size);
            for (int i = 0; i < size; i++) {
                goals.add(new Pair<>(new Pair<>(buf.readString(), buf.readString()), buf.readInt()));
            }

            boolean isRunning = buf.readBoolean();
            return new LockoutGoalsTeamsPayload(teams, goals, isRunning);
        }

        @Override
        public void encode(RegistryByteBuf buf, LockoutGoalsTeamsPayload payload) {
            // Write teams
            List<LockoutTeam> teams = payload.teams();
            buf.writeInt(teams.size());
            for (LockoutTeam team : payload.teams()) {
                buf.writeInt(team.getPlayerNames().size());
                buf.writeString(team.getColor().asString());
                for (String playerName : team.getPlayerNames()) {
                    buf.writeString(playerName);
                }
            }

            // Write goals
            buf.writeInt(payload.goals().size());
            for (Pair<Pair<String, String>, Integer> goal : payload.goals()) {
                buf.writeString(goal.getA().getA());
                buf.writeString(goal.getA().getB());
                buf.writeInt(goal.getB());
            }

            buf.writeBoolean(payload.isRunning);
        }
    };

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
