package me.marin.lockoutbutbetter;

import me.marin.lockoutbutbetter.lockout.DefaultGoalRegister;
import net.fabricmc.api.ModInitializer;

public class LockoutButBetter implements ModInitializer {
    @Override
    public void onInitialize() {
        DefaultGoalRegister.registerGoals();

    }
}
