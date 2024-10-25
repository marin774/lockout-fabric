package me.marin.lockout.mixin.server;

import me.marin.lockout.CompassItemHandler;
import me.marin.lockout.Lockout;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.LodestoneTrackerComponent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.GlobalPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;
import java.util.UUID;

@Mixin(CompassItem.class)
public class CompassItemMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    public void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (entity.getWorld().isClient) return;
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;
        if (!(entity instanceof PlayerEntity player)) return;
        if (!lockout.isLockoutPlayer(player)) return;

        if (!CompassItemHandler.isCompass(stack)) return;

        CompassItem item = (CompassItem) stack.getItem();
        int selectionNum = LockoutServer.compassHandler.currentSelection.getOrDefault(player.getUuid(), -1);
        if (selectionNum < 0) return;
        UUID selectedId = LockoutServer.compassHandler.players.get(selectionNum);
        PlayerEntity selectedPlayer = world.getServer().getPlayerManager().getPlayer(selectedId);
        if (selectedPlayer != null) {
            if (selectedPlayer.getWorld().equals(player.getWorld())) {
                stack.set(DataComponentTypes.LODESTONE_TRACKER, new LodestoneTrackerComponent(Optional.of(GlobalPos.create(world.getRegistryKey(), selectedPlayer.getBlockPos())), true));
                ci.cancel();
            }
        } else {
            stack.remove(DataComponentTypes.LODESTONE_TRACKER);
        }

        CompassItemHandler cih = LockoutServer.compassHandler;
        String trackingPlayerName = cih.playerNames.get(cih.players.get(cih.currentSelection.get(player.getUuid())));

        stack.set(DataComponentTypes.CUSTOM_NAME, Text.of(Formatting.RESET + "Tracking: " +  trackingPlayerName));
    }

}
