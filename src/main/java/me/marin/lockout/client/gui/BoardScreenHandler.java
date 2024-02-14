package me.marin.lockout.client.gui;


import me.marin.lockout.client.LockoutClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class BoardScreenHandler extends ScreenHandler {

    public BoardScreenHandler(int syncId, @SuppressWarnings("unused") PlayerInventory playerInventory) {
        super(LockoutClient.BOARD_SCREEN_HANDLER, syncId);
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return null;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

}