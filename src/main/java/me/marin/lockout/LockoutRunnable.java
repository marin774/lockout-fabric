package me.marin.lockout;

import me.marin.lockout.server.LockoutServer;

@FunctionalInterface
public interface LockoutRunnable {

    default void runTaskAfter(long ticks) {
        LockoutServer.gameStartRunnables.put(this, ticks);
    }
    void run();

}
