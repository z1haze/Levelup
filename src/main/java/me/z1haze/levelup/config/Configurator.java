package me.z1haze.levelup.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public abstract class Configurator {
    private final File file;
    protected YamlConfiguration config;

    public Configurator(JavaPlugin plugin, String fileName) {
        file = new File(plugin.getDataFolder(), fileName);

        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Failed to create file " + fileName + " due to " + e.getMessage());
                plugin.getServer().getPluginManager().disablePlugin(plugin);
            }
        }

        load();
    }

    public YamlConfiguration get() {
        return config;
    }

    public void save() throws IOException {
        config.save(file);
    }

    public void load() {
        config = YamlConfiguration.loadConfiguration(file);
    }
}
