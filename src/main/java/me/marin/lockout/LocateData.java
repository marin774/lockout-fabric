package me.marin.lockout;

import me.marin.lockout.server.LockoutServer;

/**
 * @param wasLocated true only if distance is less than {@link LockoutServer#LOCATE_SEARCH}
 * @param distance euclidean distance
 */
public record LocateData(boolean wasLocated, int distance) {
}
