package me.marin.lockout;

import me.marin.lockout.lockout.DefaultGoalRegister;
import net.fabricmc.api.ModInitializer;

public class LockoutInitializer implements ModInitializer {

    @Override
    public void onInitialize() {
        System.out.println("Registering all goals...");
        DefaultGoalRegister.registerGoals();
        System.out.println("Goals registered!");
    }

}
