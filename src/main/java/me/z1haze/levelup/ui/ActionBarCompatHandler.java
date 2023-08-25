package me.z1haze.levelup.ui;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.EnumWrappers;
import me.z1haze.levelup.LevelUp;
import org.bukkit.entity.Player;

public class ActionBarCompatHandler {
    private final LevelUp instance = LevelUp.getInstance();
    private final ActionBar actionBar;
    private static final int PAUSE_TICKS = 50;

    public ActionBarCompatHandler() {
        actionBar = instance.getActionBar();
    }

    public void registerListeners() {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();

        registerNewListener(manager);
        registerSystemChatListener(manager);
        registerChatListener(manager);
    }

    private void registerNewListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(instance, ListenerPriority.MONITOR, PacketType.Play.Server.SET_ACTION_BAR_TEXT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player p = event.getPlayer();
                PacketContainer packet = event.getPacket();
                if (packet.getMeta("LevelUp").isPresent()) return; // Ignore LevelUp action bars
                actionBar.setPaused(p.getUniqueId(), PAUSE_TICKS);
            }
        });
    }

    private void registerSystemChatListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(instance, ListenerPriority.MONITOR, PacketType.Play.Server.SYSTEM_CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player p = event.getPlayer();
                PacketContainer packet = event.getPacket();

                if (packet.getMeta("LevelUp").isPresent()) return;

                StructureModifier<Integer> integers = packet.getIntegers();

                if (integers.size() == 1) {
                    if (integers.read(0) == EnumWrappers.ChatType.GAME_INFO.getId()) {
                        actionBar.setPaused(p.getUniqueId(), PAUSE_TICKS);
                    }
                } else if (packet.getBooleans().read(0)) {
                    actionBar.setPaused(p.getUniqueId(), PAUSE_TICKS);
                }
            }
        });
    }

    private void registerChatListener(ProtocolManager manager) {
        manager.addPacketListener(new PacketAdapter(instance, ListenerPriority.MONITOR, PacketType.Play.Server.CHAT) {
            @Override
            public void onPacketSending(PacketEvent event) {
                Player p = event.getPlayer();
                PacketContainer packet = event.getPacket();

                // Make sure the chat packet is for the action bar
                if (packet.getChatTypes().read(0) != EnumWrappers.ChatType.GAME_INFO) {
                    return;
                }

                actionBar.setPaused(p.getUniqueId(), PAUSE_TICKS);
            }
        });
    }
}
