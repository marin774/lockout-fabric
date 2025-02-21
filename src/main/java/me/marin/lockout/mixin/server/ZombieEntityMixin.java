package me.marin.lockout.mixin.server;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin {

    @Unique
    private Difficulty before;

    @Inject(method = "onKilledOther", at = @At("HEAD"))
    public void setDifficulty(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        before = world.getDifficulty();
        world.getServer().setDifficulty(Difficulty.HARD, true);
    }

    @Inject(method = "onKilledOther", at = @At("RETURN"))
    public void restoreDifficulty(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        world.getServer().setDifficulty(before, true);
    }

}
