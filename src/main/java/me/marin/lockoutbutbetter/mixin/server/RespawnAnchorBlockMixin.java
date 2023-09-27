package me.marin.lockoutbutbetter.mixin.server;

import me.marin.lockoutbutbetter.Lockout;
import net.minecraft.block.BlockState;
import net.minecraft.block.RespawnAnchorBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(RespawnAnchorBlock.class)
public class RespawnAnchorBlockMixin {

    /*@Inject(method = "onUse", at = @At("RETURN"))
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (!(isChargeItem(itemStack) && canCharge(state))
                state.get(RespawnAnchorBlock.CHARGES) > 0 && cir.getReturnValue().equals(ActionResult.SUCCESS) && !world.getDimension().respawnAnchorWorks()) {
            Lockout.latestBedExplosion = player;
        }
    }*/
    
}
