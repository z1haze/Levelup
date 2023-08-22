package me.z1haze.levelup.discord;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.discord.commands.LinkCommand;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.events.session.ShutdownEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

import static me.z1haze.levelup.utils.ChatUtils.colorize;

public class BotListener extends ListenerAdapter {
    private final LevelUp instance = LevelUp.getInstance();
    private final Discord discord;
    private final YamlConfiguration config;

    public BotListener(Discord discord) {
        this.discord = discord;
        this.config = discord.getConfig().get();
    }

    private void setupCommands() {
        List<CommandData> commands = new ArrayList<>();
        SlashCommandData linkCommand = Commands.slash("link", "Link your minecraft account with discord");
        linkCommand.addOption(OptionType.STRING, "code", "The code provided to you after you ran `/discord link` in our minecraft server", true);
        commands.add(linkCommand);

        discord.getGuild().updateCommands().addCommands(commands).queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent e) {
        JDA bot = e.getJDA();
        Guild guild = null;

        // make sure bot is in the correct guild
        for (Guild g : bot.getGuilds()) {
            if (g.getId().equals(config.getString("guild-id"))) {
                guild = g;
                break;
            }
        }

        if (guild == null) {
            bot.shutdownNow();
            instance.getLogger().severe("Bot is not in the configured guild");
            return;
        }

        discord.setGuild(guild);

        String[] channels = {"chat", "link"};

        for (String value : channels) {
            if (config.getBoolean(value + ".enabled")) {
                String channelId = config.getString(value + ".channel-id");

                if (channelId == null || channelId.isEmpty()) {
                    instance.getLogger().severe(value.substring(0, 1).toUpperCase() + value.substring(1) + " enabled but no " + value + " channel id found! " + value.substring(0, 1).toUpperCase() + value.substring(1) + " will not be enabled.");
                    continue;
                }

                try {
                    if (value.equals("chat")) {
                        discord.setChatChannel(guild.getTextChannelById(channelId));
                    } else if (value.equals("link")) {
                        discord.setLinkChannel(guild.getTextChannelById(channelId));
                    }
                } catch (Exception ex) {
                    instance.getLogger().severe(value.substring(0, 1).toUpperCase() + value.substring(1) + " channel is invalid!");
                }
            }
        }

        setupCommands();

        discord.setBotReady(true);
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent e) {
        discord.setBotReady(false);
        discord.setGuild(null);
        discord.setChatChannel(null);
        instance.getLogger().info("Discord bot is stopped.");
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        if (e.getGuild().getId().equals(config.getString("guild-id"))) {
            instance.getLogger().severe("Bot left the configured guild.");
            discord.getBot().shutdownNow();
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent e) {
        if (e.getMessage().getAuthor().isBot() || e.getMessage().getAuthor().isSystem()) return;

        String fromChannelId = e.getChannel().getId();

        if (discord.getConfig().get().getBoolean("link.enabled")) {
            TextChannel linkChannel = discord.getLinkChannel();

            if (linkChannel != null && fromChannelId.equals(linkChannel.getId())) {
                e.getMessage().delete().queue();
                return;
            }
        }

        // if chat isn't enabled, ignore
        if (discord.getConfig().get().getBoolean("chat.enabled")) {
            TextChannel chatChannel = discord.getChatChannel();

            if (chatChannel != null && fromChannelId.equals(chatChannel.getId())) {
                Message m = e.getMessage();

                instance.getServer().broadcastMessage(
                        colorize(instance.getMessage("prefix")
                                + " " + instance.getMessage("discord.prefix")
                                + " &f" + m.getMember().getEffectiveName() + ": ") + m.getContentRaw()
                );
            }
        }
    }

    @Override
    public void onSlashCommandInteraction(@Nonnull SlashCommandInteractionEvent e) {
        String cmd = e.getName();

        if (cmd.equals("link")) {
            new LinkCommand(e, discord);
        }
    }
}
