package me.marin.lockout.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;

public class Networking {
    public static void registerPayloads() {
        PayloadTypeRegistry.playS2C().register(UpdateTimerPayload.ID, UpdateTimerPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(UpdateTooltipPayload.ID, UpdateTooltipPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LockoutGoalsTeamsPayload.ID, LockoutGoalsTeamsPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(StartLockoutPayload.ID, StartLockoutPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(CompleteTaskPayload.ID, CompleteTaskPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EndLockoutPayload.ID, EndLockoutPayload.CODEC);
        PayloadTypeRegistry.playS2C().register(LockoutVersionPayload.ID, LockoutVersionPayload.CODEC);

        PayloadTypeRegistry.playC2S().register(CustomBoardPayload.ID, CustomBoardPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(LockoutVersionPayload.ID, LockoutVersionPayload.CODEC);
    }
}
