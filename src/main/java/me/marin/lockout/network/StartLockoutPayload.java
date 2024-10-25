package me.marin.lockout.network;

import me.marin.lockout.Constants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public class StartLockoutPayload implements CustomPayload {
    public static final StartLockoutPayload INSTANCE = new StartLockoutPayload();
    public static final CustomPayload.Id<StartLockoutPayload> ID = new CustomPayload.Id<>(Constants.START_LOCKOUT_PACKET);
    public static final PacketCodec<RegistryByteBuf, StartLockoutPayload> CODEC = PacketCodec.unit(INSTANCE);

    private StartLockoutPayload() {}

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
