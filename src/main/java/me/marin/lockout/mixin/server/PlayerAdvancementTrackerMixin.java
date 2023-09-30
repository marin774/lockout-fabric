package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import me.marin.lockout.lockout.interfaces.VisitBiomeGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementEntry;value()Lnet/minecraft/advancement/Advancement;", ordinal = 1, shift = At.Shift.AFTER) )
    public void onGrantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof AdvancementGoal advancementGoal) {
                if (advancementGoal.getAdvancements().contains(advancement.id())) {
                    lockout.completeGoal(goal, owner);
                }
            }
        }
    }

    private static final Identifier ADVENTURING_TIME = new Identifier("minecraft", "adventure/adventuring_time");
    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementProgress;isDone()Z", ordinal = 1, shift = At.Shift.BEFORE) )
    public void onAdvancementProgress(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isRunning()) return;

        Lockout lockout = Lockout.getInstance();

        if (!advancement.id().equals(ADVENTURING_TIME)) return;
        Identifier biomeId = new Identifier(criterionName);

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof VisitBiomeGoal visitBiomeGoal) {
                if (visitBiomeGoal.getBiomes().contains(biomeId)) {
                    lockout.completeGoal(goal, owner);
                }
            }
        }

    }
}
