package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeamServer;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.interfaces.AdvancementGoal;
import me.marin.lockout.lockout.interfaces.GetUniqueAdvancementsGoal;
import me.marin.lockout.lockout.interfaces.VisitBiomeGoal;
import me.marin.lockout.server.LockoutServer;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.advancement.AdvancementEntry;
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
import java.util.Optional;

@Mixin(PlayerAdvancementTracker.class)
public abstract class PlayerAdvancementTrackerMixin {

    @Shadow
    private ServerPlayerEntity owner;

    @Redirect(method = "method_53637", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V") )
    public void onBroadcastInChat(PlayerManager instance, Text message, boolean overlay) {
        Lockout lockout = LockoutServer.lockout;

        // Prevent spectator advancements from showing in chat
        if (!Lockout.isLockoutRunning(lockout) || lockout.isLockoutPlayer(owner.getUuid())) {
            instance.broadcast(message, overlay);
        }
    }

    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/Advancement;rewards()Lnet/minecraft/advancement/AdvancementRewards;") )
    public void onGrantCriterion(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;
        if (!lockout.isLockoutPlayer(owner.getUuid())) return;
        LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(owner.getUuid());

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (goal.isCompleted()) continue;

            if (goal instanceof AdvancementGoal advancementGoal) {
                if (advancementGoal.getAdvancements().contains(advancement.id())) {
                    lockout.completeGoal(goal, owner);
                }
            }
            if (goal instanceof GetUniqueAdvancementsGoal getUniqueAdvancementsGoal) {
                Optional<AdvancementDisplay> advancementDisplay = advancement.value().display();
                if (advancementDisplay.isPresent()) {
                    getUniqueAdvancementsGoal.getTrackerMap().putIfAbsent(team, new LinkedHashSet<>());
                    getUniqueAdvancementsGoal.getTrackerMap().get(team).add(advancement.id());

                    int size = getUniqueAdvancementsGoal.getTrackerMap().get(team).size();

                    team.sendTooltipUpdate(getUniqueAdvancementsGoal);
                    if (size >= getUniqueAdvancementsGoal.getAmount()) {
                        lockout.completeGoal(goal, team);
                    }
                }
            }
        }
    }

    private static final Identifier ADVENTURING_TIME = Identifier.of("minecraft", "adventure/adventuring_time");
    @Inject(method = "grantCriterion", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancement/AdvancementProgress;isDone()Z", ordinal = 1, shift = At.Shift.BEFORE) )
    public void onAdvancementProgress(AdvancementEntry advancement, String criterionName, CallbackInfoReturnable<Boolean> cir) {
        Lockout lockout = LockoutServer.lockout;
        if (!Lockout.isLockoutRunning(lockout)) return;

        if (!advancement.id().equals(ADVENTURING_TIME)) return;
        Identifier biomeId = Identifier.of(criterionName);

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
