package me.z1haze.levelup.commands;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.OfflinePlayerArgument;
import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.LevelUpPlayer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.SimpleDateFormat;
import java.util.Date;

import static me.z1haze.levelup.utils.ChatUtils.sendColorized;

public class PlayerInfoCommand {

    public PlayerInfoCommand(BukkitCommandManager<CommandSender> commandManager) {
        LevelUp instance = LevelUp.getInstance();

        // /pinfo
        commandManager.command(
                commandManager.commandBuilder("pinfo")
                        .permission("levelup.command.pinfo")
                        .senderType(Player.class)
                        .handler(ctx -> {
                            OfflinePlayer player = (OfflinePlayer) ctx.getSender();
                            LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(player.getUniqueId());
                            sendPlayerInfo(ctx.getSender(), player, lplayer);
                        })
        );

        // /pinfo <player>
        commandManager.command(
                commandManager.commandBuilder("pinfo")
                        .permission("levelup.command.pinfo.others")
                        .argument(OfflinePlayerArgument.of("player"))
                        .handler(ctx -> {
                            OfflinePlayer player = ctx.get("player");
                            LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(player.getUniqueId());

                            if (lplayer == null) {
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("not-found-player"));
                                return;
                            }

                            sendPlayerInfo(ctx.getSender(), player, lplayer);
                        })
        );
    }

    private void sendPlayerInfo(CommandSender sender, OfflinePlayer player, LevelUpPlayer lplayer) {
        sendColorized(sender, "§bLevel: §7" + lplayer.level);
        sendColorized(sender, "§bLevel: §7" + lplayer.level);

        SimpleDateFormat dateFormatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

        sendColorized(sender, "§bFirst seen: §7" + dateFormatter.format(new Date(player.getFirstPlayed())));
        sendColorized(sender, "§bLast seen: §7" + dateFormatter.format(new Date(player.getLastPlayed())));
        sendColorized(sender, "§bDiscord linked: " + ((lplayer.discordLinked != null && lplayer.discordLinked) ? "§aYes" : "§cNo"));
    }
}
