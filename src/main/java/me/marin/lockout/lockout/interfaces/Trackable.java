package me.marin.lockout.lockout.interfaces;

import java.util.Map;

public interface Trackable<K, V> {

    Map<K, V> getTrackerMap();
}
