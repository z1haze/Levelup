package me.z1haze.levelup.config;

import org.bukkit.Location;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ConfigGeneral extends Configurator {
    public ConfigGeneral(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);

        // generic
        config.addDefault("limbo", new Location(plugin.getServer().getWorld("world"), 206.5F, 64, -244.5F, 90F, -90F));

        config.options().copyDefaults(true);

        try {
            save();
            load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
