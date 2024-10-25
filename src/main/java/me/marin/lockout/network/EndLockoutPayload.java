package me.marin.lockout.network;

import me.marin.lockout.Constants;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record EndLockoutPayload(int[] winners, long time) implements CustomPayload {
    public static final CustomPayload.Id<EndLockoutPayload> ID = new CustomPayload.Id<>(Constants.END_LOCKOUT_PACKET);
    public static final PacketCodec<RegistryByteBuf, EndLockoutPayload> CODEC = PacketCodec.tuple(
            PacketCodec.of((winners, buf) -> buf.writeIntArray(winners), PacketByteBuf::readIntArray),
            EndLockoutPayload::winners,
            PacketCodecs.LONG,
            EndLockoutPayload::time,
            EndLockoutPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
