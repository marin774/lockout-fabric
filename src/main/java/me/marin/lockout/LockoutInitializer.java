package me.marin.lockout;

import me.marin.lockout.lockout.DefaultGoalRegister;
import net.fabricmc.api.ModInitializer;

public class LockoutInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        DefaultGoalRegister.registerGoals();
    }

}
