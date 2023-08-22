package me.z1haze.levelup.discord.commands;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.LevelUpPlayer;
import me.z1haze.levelup.discord.Discord;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import static me.z1haze.levelup.utils.ChatUtils.sendColorized;

public class LinkCommand {
    private final LevelUp instance = LevelUp.getInstance();

    public LinkCommand(SlashCommandInteractionEvent e, Discord discord) {
        String code = e.getOption("code").getAsString().toUpperCase();

        if (!discord.getLinkCodes().containsKey(code)) {
            e.reply(instance.getMessage("discord.link.invalid")).setEphemeral(true).queue();
            return;
        }

        LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(discord.getLinkCodes().get(code));

        if (lplayer == null) {
            e.reply(instance.getMessage("discord.link.error")).setEphemeral(true).queue();
            return;
        }

        Member guildMember = e.getMember();

        if (guildMember == null) {
            e.reply(instance.getMessage("discord.link.error")).setEphemeral(true).queue();
            return;
        }

        boolean previouslyLinked = lplayer.discordUserId != null;
        discord.link(lplayer, guildMember);

        // reward player for linking
        if (!previouslyLinked) {
            instance.getServer().getScheduler().runTask(instance, () -> {
                ConsoleCommandSender cs = Bukkit.getConsoleSender();
                for (String rewardCommand : discord.getConfig().getLinkRewardCommands()) {
                    Bukkit.dispatchCommand(cs, rewardCommand.replace("%player%", lplayer.name));
                }
            });
        }

        // send confirmations
        e.reply(instance.getMessage("discord.link.success").replaceAll("&", "")).setEphemeral(true).queue();

        Player p = instance.getServer().getPlayer(lplayer.uuid);

        if (p != null) {
            sendColorized(p, instance.getMessage("prefix") + " " + instance.getMessage("discord.link.success"));
        }
    }
}
