package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import me.marin.lockout.lockout.interfaces.GetUniqueAdvancementsGoal;
import me.marin.lockout.lockout.interfaces.VisitBiomeGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.LinkedHashSet;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Redirect(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V") )
    public void onBroadcastInChat(PlayerManager instance, Text message, boolean overlay) {
        Lockout lockout = LockoutServer.lockout;

        // Prevent spectator advancements from showing in chat
        if (!Lockout.isLockoutRunning(lockout) || (Lockout.isLockoutRunning(lockout) && lockout.isLockoutPlayer(owner.getUuid()))) {
            instance.broadcast(message, overlay);
        }
    }

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/Advancement;getRewards()Lnet/minecraft/advancement/AdvancementRewards;") )
    public void onGrantCriterion(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;
        if (!lockout.isLockoutPlayer(owner.getUuid())) return;
        LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(owner.getUuid());

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof AdvancementGoal advancementGoal) {
                if (advancementGoal.getAdvancements().contains(advancement.getId())) {
                    lockout.completeGoal(goal, owner);
                }
            }
            if (goal instanceof GetUniqueAdvancementsGoal getUniqueAdvancementsGoal) {
                AdvancementDisplay advancementDisplay = advancement.getDisplay();
                if (advancementDisplay != null) {
                    getUniqueAdvancementsGoal.getTrackerMap().putIfAbsent(team, new LinkedHashSet<>());
                    getUniqueAdvancementsGoal.getTrackerMap().get(team).add(advancement.getId());

                    int size = getUniqueAdvancementsGoal.getTrackerMap().get(team).size();

                    team.sendTooltipUpdate(getUniqueAdvancementsGoal);
                    if (size >= getUniqueAdvancementsGoal.getAmount()) {
                        lockout.completeGoal(goal, team);
                    }
                }
            }
        }
    }

    private static final Identifier ADVENTURING_TIME = new Identifier("minecraft", "adventure/adventuring_time");
    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementProgress;isDone()Z", ordinal = 1, shift = At.Shift.BEFORE) )
    public void onAdvancementProgress(Advancement advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        if (!advancement.getId().equals(ADVENTURING_TIME)) return;
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
