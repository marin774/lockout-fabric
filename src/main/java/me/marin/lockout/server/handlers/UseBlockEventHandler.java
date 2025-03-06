package me.marin.lockout.server.handlers;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.LightCandleGoal;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.CandleBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static me.marin.lockout.server.LockoutServer.lockout;

public class UseBlockEventHandler implements UseBlockCallback {

    @Override
    public ActionResult interact(PlayerEntity player, World world, Hand hand, BlockHitResult blockHitResult) {
        if (!Lockout.isLockoutRunning(lockout)) return ActionResult.PASS;

        BlockPos blockPos = blockHitResult.getBlockPos();
        if (!CandleBlock.canBeLit(world.getBlockState(blockPos))) return ActionResult.PASS;

        ItemStack stack = player.getStackInHand(hand);
        if (!stack.isOf(Items.FLINT_AND_STEEL) && !stack.isOf(Items.FIRE_CHARGE)) return ActionResult.PASS;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof LightCandleGoal) {
                lockout.completeGoal(goal, player);
            }
        }
        return ActionResult.PASS;
    }
}
