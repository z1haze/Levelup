package me.z1haze.levelup.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class ChatUtils {

    private static final String WITH_DELIMITER = "((?<=%1$s)|(?=%1$s))";

    public static String colorize(String text) {
        String[] texts = text.split(String.format(WITH_DELIMITER, "&"));

        StringBuilder finalText = new StringBuilder();

        for (int i = 0; i < texts.length; i++){
            if (texts[i].equalsIgnoreCase("&")){
                //get the next string
                i++;
                if (texts[i].charAt(0) == '#'){
                    finalText.append(net.md_5.bungee.api.ChatColor.of(texts[i].substring(0, 7))).append(texts[i].substring(7));
                }else{
                    finalText.append(ChatColor.translateAlternateColorCodes('&', "&" + texts[i]));
                }
            }else{
                finalText.append(texts[i]);
            }
        }

        return finalText.toString();

    }

    public static void sendColorized(CommandSender sender, String str) {
        if (str != null) {
            sender.sendMessage(colorize(str));
        }
    }
}
