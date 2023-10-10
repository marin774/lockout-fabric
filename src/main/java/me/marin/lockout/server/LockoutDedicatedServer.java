package me.marin.lockout.server;

import net.fabricmc.api.DedicatedServerModInitializer;

public class LockoutDedicatedServer implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        LockoutServer.initializeServer();
    }

}
