package me.marin.lockout.server.handlers;

import me.marin.lockout.Lockout;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.server.network.ServerPlayerEntity;

import static me.marin.lockout.server.LockoutServer.compassHandler;
import static me.marin.lockout.server.LockoutServer.lockout;

public class AfterRespawnEventHandler implements ServerPlayerEvents.AfterRespawn {

    @Override
    public void afterRespawn(ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive) {
        if (!Lockout.isLockoutRunning(lockout)) return;
        if (lockout.isSoloBlackout()) return;
        if (!lockout.isLockoutPlayer(newPlayer.getUuid())) return;
        if (alive) return; // end exit portal

        int slot = compassHandler.compassSlots.getOrDefault(newPlayer.getUuid(), 0);
        if (slot == 40) {
            newPlayer.getInventory().setStack(40, compassHandler.newCompass());
        }
        if (slot >= 0 && slot <= 35) {
            newPlayer.getInventory().setStack(slot, compassHandler.newCompass());
        }
    }
}
