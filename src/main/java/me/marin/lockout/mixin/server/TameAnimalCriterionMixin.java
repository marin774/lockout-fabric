package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.TameAnimalGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.TameAnimalCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TameAnimalCriterion.class)
public class TameAnimalCriterionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onTameAnimal(ServerPlayerEntity player, AnimalEntity entity, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (!(goal instanceof TameAnimalGoal tameAnimalGoal)) continue;
            if (goal.isCompleted()) continue;

            if (entity.getType().equals(tameAnimalGoal.getAnimal())) {
                lockout.completeGoal(tameAnimalGoal, player);
                return;
            }
        }
    }

}
