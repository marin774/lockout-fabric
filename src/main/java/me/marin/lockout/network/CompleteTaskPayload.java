package me.marin.lockout.network;

import me.marin.lockout.Constants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record CompleteTaskPayload(String goal, int teamIndex) implements CustomPayload {
    public static final Id<CompleteTaskPayload> ID = new Id<>(Constants.COMPLETE_TASK_PACKET);
    public static final PacketCodec<RegistryByteBuf, CompleteTaskPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING,
            CompleteTaskPayload::goal,
            PacketCodecs.INTEGER,
            CompleteTaskPayload::teamIndex,
            CompleteTaskPayload::new);

    @Override
    public Id<? extends CustomPayload> getId() {
        return ID;
    }
}
