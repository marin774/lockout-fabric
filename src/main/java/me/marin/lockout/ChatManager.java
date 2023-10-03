package me.marin.lockout;

import net.minecraft.entity.player.PlayerEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ChatManager {

    public enum Type {
        LOCAL,
        TEAM
    }

    private static final Map<UUID, Type> map = new HashMap<>();

    public static void setChat(PlayerEntity player, Type type) {
        map.put(player.getUuid(), type);
    }

    public static Type getChat(PlayerEntity player) {
        return map.getOrDefault(player.getUuid(), Type.LOCAL);
    }

}
