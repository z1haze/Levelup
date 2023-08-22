package me.z1haze.levelup.listeners;

import me.z1haze.levelup.LevelUp;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class LevelUpPlayerListener implements Listener {
    private final LevelUp instance = LevelUp.getInstance();

    @EventHandler
    public void onPlayerJoined(PlayerJoinEvent e) {
        instance.getPlayerDataManager().playerJoined(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        instance.getPlayerDataManager().playerQuit(e.getPlayer());
    }
}
