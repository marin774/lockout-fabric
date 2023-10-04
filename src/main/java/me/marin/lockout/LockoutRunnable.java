package me.marin.lockout;

import me.marin.lockout.server.LockoutServer;

public interface LockoutRunnable {

    default void runTaskAfter(int ticks) {
        LockoutServer.gameStartRunnables.put(this, ticks);
    }
    void run();

}
