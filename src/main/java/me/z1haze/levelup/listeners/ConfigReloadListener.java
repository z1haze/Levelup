package me.z1haze.levelup.listeners;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.discord.Discord;
import me.z1haze.levelup.events.ConfigChangeEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ConfigReloadListener implements Listener {
    private final LevelUp instance = LevelUp.getInstance();
    private final Discord discord;

    public ConfigReloadListener() {
        discord = instance.getDiscord();
    }

    @EventHandler
    public void onConfigReload(ConfigChangeEvent e) {
        if (discord.getBotReady()) {
            discord.getBot().shutdownNow();
        }

        instance.getDiscord().initialize();
    }
}
