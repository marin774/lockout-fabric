package me.marin.lockout.mixin.server;

import me.marin.lockout.CompassItemHandler;
import me.marin.lockout.Lockout;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CompassItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(CompassItem.class)
public class CompassItemMixin {

    @Inject(method = "inventoryTick", at = @At("HEAD"), cancellable = true)
    public void onInventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.exists()) return;

        if (!(entity instanceof PlayerEntity player)) return;

        if (stack == null || stack.isEmpty()) return;
        if (stack.getItem() != Items.COMPASS) return;
        CompassItem item = (CompassItem) stack.getItem();
        int selectionNum = CompassItemHandler.INSTANCE.currentSelection.getOrDefault(player.getUuid(), -1);
        if (selectionNum < 0) return;
        UUID selectedId = CompassItemHandler.INSTANCE.players.get(selectionNum);
        PlayerEntity selectedPlayer = world.getServer().getPlayerManager().getPlayer(selectedId);
        if (selectedPlayer != null) {
            if (selectedPlayer.getWorld().equals(player.getWorld())) {
                item.writeNbt(world.getRegistryKey(), selectedPlayer.getBlockPos(), stack.getOrCreateNbt());
                ci.cancel();
            }
        } else {
            NbtCompound compound = stack.getOrCreateNbt();
            compound.remove(CompassItem.LODESTONE_DIMENSION_KEY);
            compound.remove(CompassItem.LODESTONE_TRACKED_KEY);
        }

        stack.setCustomName(Text.of("Tracking: " + CompassItemHandler.INSTANCE.playerNames.get(CompassItemHandler.INSTANCE.players.get(CompassItemHandler.INSTANCE.currentSelection.get(player.getUuid()))) ));
    }

}
