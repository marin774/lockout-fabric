package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.BreedAnimalGoal;
import me.marin.lockout.lockout.interfaces.BreedUniqueAnimalsGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.advancement.criterion.BredAnimalsCriterion;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.LinkedHashSet;

@Mixin(BredAnimalsCriterion.class)
public class BredAnimalsCriterionMixin {

    @Inject(method = "trigger", at = @At("HEAD"))
    public void onBreedAnimal(ServerPlayerEntity player, AnimalEntity parent, AnimalEntity partner, @Nullable PassiveEntity child, CallbackInfo ci) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        if (!lockout.isLockoutPlayer(player.getUuid())) return;

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof BreedAnimalGoal breedAnimalGoal) {
                if (parent.getType().equals(breedAnimalGoal.getAnimal())) {
                    lockout.completeGoal(breedAnimalGoal, player);
                }
            } else if (goal instanceof BreedUniqueAnimalsGoal breedUniqueAnimalsGoal) {
                LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(player.getUuid());
                lockout.bredAnimalTypes.computeIfAbsent(team, t -> new LinkedHashSet<>());
                lockout.bredAnimalTypes.get(team).add(parent.getType());
                int size = lockout.bredAnimalTypes.get(team).size();

                team.sendLoreUpdate(breedUniqueAnimalsGoal);
                if (size >= breedUniqueAnimalsGoal.getAmount()) {
                    lockout.completeGoal(breedUniqueAnimalsGoal, team);
                }
            }

        }
    }

}
