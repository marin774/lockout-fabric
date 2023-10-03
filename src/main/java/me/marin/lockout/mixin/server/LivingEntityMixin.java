package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.Deal400DamageGoal;
import me.marin.lockout.lockout.goals.opponent.OpponentCatchesOnFireGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class LivingEntityMixin {

    @Inject(method = "damage", at = @At("RETURN"))
    public void onDamage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;
        if (!(source.getAttacker() instanceof PlayerEntity player) || !cir.getReturnValue()) return;

        Lockout lockout = Lockout.getInstance();

        // damageDealt
        lockout.damageDealt.putIfAbsent(player.getUuid(), 0d);
        lockout.damageDealt.merge(player.getUuid(), (double)amount, Double::sum);

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof Deal400DamageGoal) {
                if (lockout.damageDealt.get(player.getUuid()) >= 400) {
                    lockout.completeGoal(goal, player);
                }
            }
        }
    }

}
