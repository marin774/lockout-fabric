package me.marin.lockout.network;

import me.marin.lockout.Constants;
import me.marin.lockout.LockoutInitializer;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.packet.CustomPayload;

public record LockoutVersionPayload(String version) implements CustomPayload {

    public static final CustomPayload.Id<LockoutVersionPayload> ID = new CustomPayload.Id<>(Constants.LOCKOUT_VERSION_PACKET);
    public static final PacketCodec<RegistryByteBuf, LockoutVersionPayload> CODEC = PacketCodec.tuple(
            PacketCodec.of((version, buf) -> buf.writeString(LockoutInitializer.MOD_VERSION.getFriendlyString()), PacketByteBuf::readString),
            LockoutVersionPayload::version,
            LockoutVersionPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }

}
