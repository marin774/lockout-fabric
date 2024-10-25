package me.marin.lockout.network;

import me.marin.lockout.Constants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record UpdateTimerPayload(long ticks) implements CustomPayload {
    public static final CustomPayload.Id<UpdateTimerPayload> ID = new CustomPayload.Id<>(Constants.UPDATE_TIMER_PACKET);
    public static final PacketCodec<RegistryByteBuf, UpdateTimerPayload> CODEC = PacketCodec.tuple(PacketCodecs.LONG, UpdateTimerPayload::ticks, UpdateTimerPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
