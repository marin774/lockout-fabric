package me.marin.lockout.network;

import me.marin.lockout.Constants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;
import oshi.util.tuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public record CustomBoardPayload(Optional<List<Pair<String, String>>> boardOrClear) implements CustomPayload {
    public static final CustomPayload.Id<CustomBoardPayload> ID = new CustomPayload.Id<>(Constants.CUSTOM_BOARD_PACKET);
    public static final PacketCodec<RegistryByteBuf, CustomBoardPayload> CODEC = PacketCodec.tuple(PacketCodec.of(
            (boardOrClear, buf) -> {
                buf.writeInt(boardOrClear.map(pairs -> (int) Math.sqrt(pairs.size())).orElse(0));
                if (boardOrClear.isEmpty()) return;
                var board = boardOrClear.get();
                for (var goal : board) {
                    buf.writeString(goal.getA());
                    buf.writeString(goal.getB());
                }
            },
            (buf) -> {
                int size = buf.readInt();
                if (size == 0) return Optional.empty();
                List<Pair<String, String>> goals = new ArrayList<>();
                for (int i = 0; i < size * size; i++) {
                    String goalId = buf.readString();
                    goals.add(new Pair<>(goalId, buf.readString()));
                }
                return Optional.of(goals);
            }
    ), CustomBoardPayload::boardOrClear, CustomBoardPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
