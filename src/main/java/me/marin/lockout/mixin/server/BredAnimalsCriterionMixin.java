package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import me.marin.lockout.lockout.interfaces.BreedUniqueAnimalsGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;

@Mixin(BredAnimalsCriterion.class)
public class BredAnimalsCriterionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onBreedAnimal(ServerPlayerEntity player, AnimalEntity parent, AnimalEntity partner, @Nullable PassiveEntity child, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof BreedAnimalGoal breedAnimalGoal) {
                if (parent.getType().equals(breedAnimalGoal.getAnimal())) {
                    lockout.completeGoal(breedAnimalGoal, player);
                }
            } else if (goal instanceof BreedUniqueAnimalsGoal breedUniqueAnimalsGoal) {
                lockout.bredAnimalTypes.computeIfAbsent(player.getUuid(), player_ -> new HashSet<>());
                lockout.bredAnimalTypes.get(player.getUuid()).add(parent.getType());
                int size = lockout.bredAnimalTypes.get(player.getUuid()).size();

                if (size >= breedUniqueAnimalsGoal.getAmount()) {
                    lockout.completeGoal(breedUniqueAnimalsGoal, player);
                }
            }

        }
    }

}
