package me.z1haze.levelup.config;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;

public class ConfigMessages extends Configurator {
    public ConfigMessages(JavaPlugin plugin, String fileName) {
        super(plugin, fileName);

        config.addDefault("prefix", "&7L&b↑ &8»");
        config.addDefault("no-permission", "&cYou don't have permission for that!");
        config.addDefault("no-player", "&cThis command cannot be executed by players!");
        config.addDefault("no-console", "&cThis command cannot be executed from the console!");
        config.addDefault("not-found-player", "&cPlayer not found!");
        config.addDefault("reloaded", "&aConfigurations reloaded!");
        config.addDefault("disposal", "&7Opening Disposal...");
        config.addDefault("join", "&7[&a+&7] &b&l%player%");
        config.addDefault("quit", "&7[&c-&7] &b&l%player%");

        config.addDefault("flight.enabled", "&7Flight &aenabled");
        config.addDefault("flight.enabled-other", "&7Flight &aenabled &7for &b%player%");
        config.addDefault("flight.disabled", "&7Flight &cdisabled");
        config.addDefault("flight.disabled-other", "&7Flight &cdisabled &7for &b%player%");
        config.addDefault("flight.not-allowed", "&cYou do not have access to fly in this claim and have been teleported to a safe spot on ground.");
        config.addDefault("flight.not-allowed-other", "&cFlight is not allowed in their current claim.");

        config.addDefault("discord.prefix", "&bDiscord &8»");
        config.addDefault("discord.invite", "&b{your discord invite url}");

        config.addDefault("discord.link.reminder", "&7Earn &a&l100 GEMS &7when linking your discord account! Type &b/discord link &7to get started.");
        config.addDefault("discord.link.instructions", "&7Link your account by typing &b/link %code% &7in the &b#account-link &7channel on discord.");
        config.addDefault("discord.link.success", "&aYour account was successfully linked!");
        config.addDefault("discord.link.error", "Your account was not be linked. Please re-attempt the linking process.");
        config.addDefault("discord.link.invalid", "The provided code was invalid. Please re-attempt the linking process.");
        config.addDefault("discord.link.exists", "&7Your account is already linked. Contact an administrator for additional assistance.");
        config.addDefault("discord.link.unlinked", "&aYour account was successfully unlinked!");

        config.options().copyDefaults(true);

        try {
            save();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
