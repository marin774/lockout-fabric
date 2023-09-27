package me.marin.lockoutbutbetter.client.gui;


import me.marin.lockoutbutbetter.client.LockoutButBetterClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandler;

public class BoardScreenHandler extends ScreenHandler {

    public BoardScreenHandler(int syncId, PlayerInventory playerInventory) {
        super(LockoutButBetterClient.BOARD_SCREEN_HANDLER, syncId);
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