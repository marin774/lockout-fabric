package me.marin.lockoutbutbetter.server;

import me.marin.lockoutbutbetter.Lockout;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.text.Text;

import static net.minecraft.server.command.CommandManager.literal;

public class LockoutButBetterServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            var lockout = literal("lockout").executes(context -> {
                context.getSource().sendMessage(Text.literal("Called /lockout with no arguments"));

                return 1;
            }).build();

            dispatcher.getRoot().addChild(lockout);
        });
    }

}
