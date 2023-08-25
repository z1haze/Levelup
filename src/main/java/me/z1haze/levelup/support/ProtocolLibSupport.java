package me.z1haze.levelup.support;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.entity.Player;

public class ProtocolLibSupport {
    private static ProtocolManager protocolManager;

    public ProtocolLibSupport() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    public void sendNewActionBar(Player p, String message) {
        PacketContainer packet = protocolManager.createPacket(PacketType.Play.Server.SET_ACTION_BAR_TEXT);
        packet.getChatComponents().write(0, WrappedChatComponent.fromLegacyText(message));
        packet.setMeta("LevelUp", true); // Mark packet as from LevelUp
        protocolManager.sendServerPacket(p, packet);
    }
}
