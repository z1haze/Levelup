package me.z1haze.levelup.listeners;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.discord.Discord;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.awt.*;

public class MessagesToDiscordListener implements Listener {
    private final Discord discord;

    public MessagesToDiscordListener() {
        LevelUp instance = LevelUp.getInstance();
        discord = instance.getDiscord();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        discord.send(p, p.getDisplayName() + " joined the server!", true, Color.GREEN);
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        discord.send(p, p.getDisplayName() + " died!", true, Color.RED);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        discord.send(p, p.getDisplayName() + " left the server!", true, Color.YELLOW);
    }

    @EventHandler(ignoreCancelled = true)
    public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
        discord.send(e.getPlayer(), e.getMessage(), false, Color.GRAY);
    }
}
