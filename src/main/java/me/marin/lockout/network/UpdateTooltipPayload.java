package me.marin.lockout.network;

import me.marin.lockout.Constants;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;

public record UpdateTooltipPayload(String goal, String tooltip) implements CustomPayload {
    public static final CustomPayload.Id<UpdateTooltipPayload> ID = new CustomPayload.Id<>(Constants.UPDATE_TOOLTIP);
    public static final PacketCodec<RegistryByteBuf, UpdateTooltipPayload> CODEC = PacketCodec.tuple(PacketCodecs.STRING,
            UpdateTooltipPayload::goal,
            PacketCodecs.STRING,
            UpdateTooltipPayload::tooltip,
            UpdateTooltipPayload::new);

    @Override
    public CustomPayload.Id<? extends CustomPayload> getId() {
        return ID;
    }
}
