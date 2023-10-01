package me.marin.lockout;

public record LocateData(boolean wasLocated, boolean isInRequiredDistance, int distance) {
}
