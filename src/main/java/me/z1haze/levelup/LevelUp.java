package me.z1haze.levelup;

import me.z1haze.levelup.commands.Commands;
import me.z1haze.levelup.config.*;
import me.z1haze.levelup.discord.Discord;
import me.z1haze.levelup.listeners.*;
import me.z1haze.levelup.managers.PermissionsManager;
import me.z1haze.levelup.quests.Quests;
import me.z1haze.levelup.support.ProtocolLibSupport;
import me.z1haze.levelup.ui.ActionBar;
import me.z1haze.levelup.ui.ActionBarCompatHandler;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class LevelUp extends JavaPlugin {

    public static LevelUp instance;
    private final Map<String, Configurator> configs = new HashMap<>();
    private PlayerDataManager playerDataManager;
    private PermissionsManager permissionsManager;
    private Discord discord;
    private ProtocolLibSupport protocolLibSupport;
    private ActionBar actionBar;

    public static LevelUp getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;

        initConfigs();

        protocolLibSupport = new ProtocolLibSupport();
        permissionsManager = new PermissionsManager();
        playerDataManager = new PlayerDataManager();
        discord = new Discord();

        new Quests();
        new Placeholders(this).register();
        new Commands().registerCommands();

        registerEvents();

        new ActionBarCompatHandler().registerListeners();
        actionBar.startUpdateActionBar();
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();

        // load this first, so we can get a record of their player data in memory
        pm.registerEvents(new LevelUpPlayerListener(), this);

        // control when/where players can fly, order does not matter
        pm.registerEvents(new PlayerFlightListener(), this);

        // send chat message to discord. must be after discord is initialized
        pm.registerEvents(new MessagesToDiscordListener(), this);

        // sync a linked player's groups with discord roles. must be after discord is initialized
        pm.registerEvents(new DiscordRoleSyncListener(), this);

        // remove loot that shouldn't be in random loot chests caused by advanced enchantments. order does not matter
        pm.registerEvents(new PlayerLootListener(), this);

        // listen for config updates. order does not matter
        pm.registerEvents(new ConfigReloadListener(), this);

        actionBar = new ActionBar();
    }

    public Discord getDiscord() {
        return discord;
    }

    public ProtocolLibSupport getProtocolLibSupport() {
        return protocolLibSupport;
    }

    public ActionBar getActionBar() {
        return actionBar;
    }

    public String getMessage(String name) {
        return configs.get("messages").get().getString(name);
    }

    private void initConfigs() {
        configs.put("config", new ConfigGeneral(this, "config.yml"));
        configs.put("messages", new ConfigMessages(this, "messages.yml"));
        configs.put("discord", new ConfigDiscord(this, "discord.yml"));
        configs.put("ae", new ConfigAE(this, "ae.yml"));
    }

    public Map<String, Configurator> getConfigs() {
        return configs;
    }

    public Configurator getConfig(String name) {
        return configs.get(name);
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public PermissionsManager getPermissionsManager() {
        return permissionsManager;
    }
}
