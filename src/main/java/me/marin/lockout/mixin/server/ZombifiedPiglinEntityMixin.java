package me.marin.lockout.mixin.server;

import me.marin.lockout.Lockout;
import me.marin.lockout.lockout.Goal;
import me.marin.lockout.lockout.goals.misc.AngerZombifiedPiglinGoal;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.mob.ZombifiedPiglinEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ZombifiedPiglinEntity.class)
public class ZombifiedPiglinEntityMixin {

    @Inject(method = "setAngryAt", at = @At("HEAD"))
    public void setAngryAt(UUID angryAt, CallbackInfo ci) {
        if (FabricLoader.getInstance().getEnvironmentType() != EnvType.SERVER) return;
        if (!Lockout.isLockoutRunning()) return;
        ServerPlayerEntity player = ((ZombifiedPiglinEntity) (Object) this).getServer().getPlayerManager().getPlayer(angryAt);

        if (player == null) {
            System.err.println("Player with UUID could not be set as angry: " + angryAt);
            return;
        }

        Lockout lockout = Lockout.getInstance();

        for (Goal goal : lockout.getBoard().getGoals()) {
            if (goal == null) continue;
            if (!(goal instanceof AngerZombifiedPiglinGoal)) continue;
            if (goal.isCompleted()) continue;

            lockout.completeGoal(goal, player);
            return;
        }
    }

}
