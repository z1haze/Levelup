package me.z1haze.levelup.commands;

import cloud.commandframework.Command;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.meta.CommandMeta;
import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.LevelUpPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.z1haze.levelup.utils.ChatUtils.sendColorized;

public class DiscordCommand {
    private final LevelUp instance = LevelUp.getInstance();

    public DiscordCommand(BukkitCommandManager<CommandSender> commandManager) {
        // /discord
        Command.Builder<CommandSender> builder = commandManager.commandBuilder("discord")
                .meta(CommandMeta.DESCRIPTION, "Get the link to the discord server")
                .permission("levelup.command.discord")
                .handler(ctx -> {
                    sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("discord.invite"));

                    if (ctx.getSender() instanceof Player) {
                        LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(((Player) ctx.getSender()).getUniqueId());

                        if (lplayer.discordLinked == null || !lplayer.discordLinked) {
                            sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("discord.link.reminder"));
                        }
                    }
                });

        commandManager.command(builder);

        // /discord link
        commandManager.command(
                builder.literal("link")
                        .meta(CommandMeta.DESCRIPTION, "Link your minecraft account with your discord account")
                        .senderType(Player.class)
                        .permission("levelup.command.discord.link")
                        .handler(ctx -> {
                            LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(((Player) ctx.getSender()).getUniqueId());

                            if (lplayer.discordLinked != null && lplayer.discordLinked) {
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("discord.link.exists"));
                            } else {
                                String code = instance.getDiscord().generateLinkCode((Player) ctx.getSender());
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("discord.link.instructions").replace("%code%", code));
                            }
                        })
        );

        // /discord unlink
        commandManager.command(
                builder.literal("unlink")
                        .senderType(Player.class)
                        .meta(CommandMeta.DESCRIPTION, "Unlink your minecraft account from your discord account")
                        .permission("levelup.command.discord.unlink")
                        .handler(ctx -> {
                            LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(((Player) ctx.getSender()).getUniqueId());

                            if (lplayer.discordLinked != null && lplayer.discordLinked) {
                                instance.getDiscord().unlink(lplayer);
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("discord.link.unlinked"));
                            }
                        })
        );
    }
}
