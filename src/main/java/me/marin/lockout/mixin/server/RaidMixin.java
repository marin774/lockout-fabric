package me.marin.lockout.mixin.server;

import net.minecraft.village.raid.Raid;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Raid.class)
public class RaidMixin {

    @Inject(method = "getMaxWaves", at = @At("HEAD"), cancellable = true)
    public void getMaxWaves(Difficulty difficulty, CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(5);
    }

    @Redirect(method = "getBonusCount", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/LocalDifficulty;getGlobalDifficulty()Lnet/minecraft/world/Difficulty;"))
    public Difficulty setDifficulty(LocalDifficulty instance) {
        return Difficulty.NORMAL;
    }

}
