package me.marin.lockout.mixin.server;

import me.marin.lockout.CompassItemHandler;
import me.marin.lockout.Lockout;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class ServerPlayerEntityMixin {

    @Inject(method = "onDeath", at = @At("HEAD"))
    public void onDeath(DamageSource damageSource, CallbackInfo ci) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        PlayerEntity player = (PlayerEntity) (Object) this;
        if (player.getWorld().isClient) return;

        int i = 0;
        for (ItemStack item : player.getInventory().main) {
            if (CompassItemHandler.isCompass(item)) {
                LockoutServer.compassHandler.compassSlots.put(player.getUuid(), i);
                return;
            }
            i++;
        }
        if (CompassItemHandler.isCompass(player.getInventory().offHand.getFirst())) {
            LockoutServer.compassHandler.compassSlots.put(player.getUuid(), 40);
        }
    }

}
