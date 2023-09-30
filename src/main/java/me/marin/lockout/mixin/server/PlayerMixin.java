package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.status_effect.RemoveStatusEffectUsingMilkGoal;
import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;
import me.marin.lockout.lockout.interfaces.IncrementStatGoal;
import me.marin.lockout.lockout.interfaces.ReachXPLevelGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashSet;
import java.util.Objects;

@Mixin(PlayerEntity.class)
public class PlayerMixin {

    @Inject(method="eatFood", at = @At("HEAD"))
    public void onEat(World world, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;

        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof ConsumeItemGoal consumeItemGoal) {
                if (consumeItemGoal.getItem().equals(itemStack.getItem())) {
                    lockout.completeGoal(goal, player);
                }
            }
            if (goal instanceof EatUniqueFoodsGoal eatUniqueFoodsGoal) {
                eatUniqueFoodsGoal.getTrackerMap().putIfAbsent(player, new HashSet<>());
                FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
                if (foodComponent != null) {
                    eatUniqueFoodsGoal.getTrackerMap().get(player).add(foodComponent);

                    int size = eatUniqueFoodsGoal.getTrackerMap().get(player).size();
                    if (size >= eatUniqueFoodsGoal.getAmount()) {
                        lockout.completeGoal(goal, player);
                    }
                }

            }
            if (goal instanceof RemoveStatusEffectUsingMilkGoal) {
                if (itemStack.getItem().equals(Items.MILK_BUCKET)) {
                    if (player.getStatusEffects().size() > 0) {
                        lockout.completeGoal(goal, player);
                    }
                }
            }

        }

    }

    @Inject(method = "incrementStat(Lnet/minecraft/util/Identifier;)V", at = @At("HEAD"))
    public void onIncrementStat(Identifier stat, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;
            if (!(goal instanceof IncrementStatGoal incrementStatGoal)) continue;

            if (incrementStatGoal.getStats().contains(stat)) {
                lockout.completeGoal(goal, player);
            }
        }
    }

    @Inject(method = "addExperienceLevels", at = @At("TAIL"))
    public void onExperienceLevelUp(int levels, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof ReachXPLevelGoal reachXPLevelGoal) {
                if (player.experienceLevel >= reachXPLevelGoal.getAmount()) {
                    lockout.completeGoal(goal, player);
                }
            }
        }
    }

}
