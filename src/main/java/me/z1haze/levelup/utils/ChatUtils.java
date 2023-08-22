package me.z1haze.levelup.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {
    public static String colorize(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

    public static void sendColorized(CommandSender sender, String str) {
        if (str != null) {
            sender.sendMessage(colorize(str));
        }
    }

}
