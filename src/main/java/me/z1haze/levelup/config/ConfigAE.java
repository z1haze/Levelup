package me.z1haze.levelup.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ConfigAE extends Configurator {
    public ConfigAE(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);

        config.addDefault("disallowed-enchantment-groups-in-loot", List.of(
                "LEGENDARY")
        );

        config.options().copyDefaults(true);

        try {
            save();
            load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public List<String> getDisallowedEnchantmentGroupsInLoot() {
        return this.config.getStringList("disallowed-enchantment-groups-in-loot");
    }
}
