package me.z1haze.levelup.managers;

import me.z1haze.levelup.LevelUp;
import net.luckperms.api.LuckPerms;
import org.bukkit.plugin.RegisteredServiceProvider;

public class PermissionsManager {
    private LuckPerms luckPerms;

    public PermissionsManager() {
        LevelUp instance = LevelUp.getInstance();
        RegisteredServiceProvider<LuckPerms> provider = instance.getServer().getServicesManager().getRegistration(LuckPerms.class);

        if (provider == null) {
            instance.getLogger().severe("Could not find LuckPerms! Disabling plugin...");
            instance.getServer().getPluginManager().disablePlugin(instance);
            return;
        }

        luckPerms = provider.getProvider();
    }

    public LuckPerms getLuckPerms() {
        return luckPerms;
    }
}
