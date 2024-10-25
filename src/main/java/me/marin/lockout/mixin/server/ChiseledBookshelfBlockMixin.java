package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.FillChiseledBookshelfGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.block.BlockState;
import net.minecraft.block.ChiseledBookshelfBlock;
import net.minecraft.block.entity.ChiseledBookshelfBlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChiseledBookshelfBlock.class)
public class ChiseledBookshelfBlockMixin {

    @Inject(method = "onUse", at = @At("RETURN"))
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit, CallbackInfoReturnable<ActionResult> cir) {
        if (world.isClient) return;
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        ChiseledBookshelfBlockEntity blockEntity = (ChiseledBookshelfBlockEntity) world.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getFilledSlotCount() < 6) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof FillChiseledBookshelfGoal) {
                lockout.completeGoal(goal, player);
            }
        }
    }

}
