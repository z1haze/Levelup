package me.z1haze.levelup.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ConfigDiscord extends Configurator {

    public ConfigDiscord(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);

        config.addDefault("bot-token", "your bot token");
        config.addDefault("guild-id", "your discord guild id");
        config.addDefault("chat.enabled", true);
        config.addDefault("chat.channel-id", "the channel id to stream messages to");
        config.addDefault("link.enabled", true);
        config.addDefault("link.channel-id", "the channel id for account link commands");
        config.addDefault("link.rewards", Arrays.asList(
                "give %player% diamond 1",
                "give %player% iron_ingot 1")
        );
        config.addDefault("link.group-map", Map.ofEntries(
                Map.entry("group-name", "discord-role-id")
        ));

        config.options().copyDefaults(true);

        try {
            save();
            load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> getGroupMap() {
        return config.getConfigurationSection("link.group-map").getValues(false)
                .entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
    }

    public List<String> getLinkRewardCommands() {
        return config.getStringList("link.rewards");
    }
}
