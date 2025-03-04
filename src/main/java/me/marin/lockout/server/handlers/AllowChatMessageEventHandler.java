package me.marin.lockout.server.handlers;

import me.marin.lockout.ChatManager;
import me.marin.lockout.Lockout;
import me.marin.lockout.LockoutTeamServer;
import net.fabricmc.fabric.api.message.v1.ServerMessageEvents;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import static me.marin.lockout.server.LockoutServer.lockout;

public class AllowChatMessageEventHandler implements ServerMessageEvents.AllowChatMessage {

    @Override
    public boolean allowChatMessage(SignedMessage message, ServerPlayerEntity sender, MessageType.Parameters parameters) {
        if (ChatManager.getChat(sender) == ChatManager.Type.TEAM) {
            String m = "[Team Chat] " + Formatting.RESET + "<" + sender.getName().getString() + "> " + message.getContent().getString();
            if (Lockout.isLockoutRunning(lockout)) {
                LockoutTeamServer team = (LockoutTeamServer) lockout.getPlayerTeam(sender.getUuid());
                team.sendMessage(team.getColor() + m);
            } else {
                Team team = sender.getScoreboardTeam();
                if (team == null) {
                    return true;
                }
                MinecraftServer server = sender.getServer();
                PlayerManager pm = server.getPlayerManager();

                team.getPlayerList().stream().filter(p -> pm.getPlayer(p) != null).map(pm::getPlayer).forEach(p ->{
                    p.sendMessage(Text.literal(team.getColor() + m));
                });
            }
            return false;
        }
        return true;
    }
}
