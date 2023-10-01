package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.Take200DamageGoal;
import me.marin.lockout.lockout.goals.opponent.*;
import me.marin.lockout.lockout.goals.status_effect.RemoveStatusEffectUsingMilkGoal;
import me.marin.lockout.lockout.interfaces.ConsumeItemGoal;
import me.marin.lockout.lockout.interfaces.EatUniqueFoodsGoal;
import me.marin.lockout.lockout.interfaces.IncrementStatGoal;
import me.marin.lockout.lockout.interfaces.ReachXPLevelGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.thrown.EggEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
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

    @Inject(method = "collideWithEntity", at = @At("HEAD"))
    public void onCollide(Entity entity, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;

        Lockout lockout = Lockout.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof OpponentHitBySnowballGoal) {
                if (entity instanceof SnowballEntity snowballEntity) {
                    if (snowballEntity.getOwner() instanceof PlayerEntity shooter && !Objects.equals(player, shooter)) {
                        lockout.opponentCompletedGoal(goal, player, shooter.getName().getString() + " hit " + player.getName().getString() + " with a Snowball.");
                    }
                }
            }
            if (goal instanceof OpponentHitByEggGoal) {
                if (entity instanceof EggEntity snowballEntity) {
                    if (snowballEntity.getOwner() instanceof PlayerEntity shooter && !Objects.equals(player, shooter)) {
                        lockout.opponentCompletedGoal(goal, player, shooter.getName().getString() + " hit " + player.getName().getString() + " with an Egg.");
                    }
                }
            }
        }
    }

    @Inject(method = "damage", at = @At("RETURN"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;
        if (!cir.getReturnValue()) return;

        Lockout lockout = Lockout.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        lockout.damageTaken.putIfAbsent(player.getUuid(), 0d);
        lockout.damageTaken.merge(player.getUuid(), (double)amount, Double::sum);

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof Take200DamageGoal) {
                if (lockout.damageTaken.get(player.getUuid()) >= 200.0) {
                    lockout.completeGoal(goal, player);
                }
            }
            if (goal instanceof OpponentTakesFallDamageGoal) {
                if (source.isOf(DamageTypes.FALL)) {
                    lockout.opponentCompletedGoal(goal, player, player.getName().getString() + " took fall damage.");
                }
            }
            if (goal instanceof OpponentTakes100DamageGoal) {
                if (lockout.damageTaken.get(player.getUuid()) >= 100) {
                    lockout.opponentCompletedGoal(goal, player, player.getName().getString() + " took 100 damage.");
                }
            }
        }
    }

    @Inject(method="eatFood", at = @At("HEAD"))
    public void onEat(World world, ItemStack itemStack, CallbackInfoReturnable<ItemStack> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;

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
                eatUniqueFoodsGoal.getTrackerMap().putIfAbsent(player.getUuid(), new HashSet<>());
                FoodComponent foodComponent = itemStack.getItem().getFoodComponent();
                if (foodComponent != null) {
                    eatUniqueFoodsGoal.getTrackerMap().get(player.getUuid()).add(foodComponent);

                    int size = eatUniqueFoodsGoal.getTrackerMap().get(player.getUuid()).size();
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
        if (!Lockout.isLockoutRunning()) return;

        Lockout lockout = Lockout.getInstance();
        PlayerEntity player = (PlayerEntity) (Object) this;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;
            if (!(goal instanceof IncrementStatGoal incrementStatGoal)) continue;

            if (incrementStatGoal.getStats().contains(stat)) {
                if (goal instanceof OpponentJumpsGoal) {
                    lockout.opponentCompletedGoal(goal, player, player.getName().getString() + " jumped.");
                } else {
                    lockout.completeGoal(goal, player);
                }
            }
        }
    }

    @Inject(method = "addExperienceLevels", at = @At("TAIL"))
    public void onExperienceLevelUp(int levels, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;

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
