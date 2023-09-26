package me.marin.lockoutbutbetter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Lockout {

    private static final Logger logger = LogManager.getLogger("Lockout");
    public static void log(String message) {
        logger.log(Level.INFO, message);
    }
}
