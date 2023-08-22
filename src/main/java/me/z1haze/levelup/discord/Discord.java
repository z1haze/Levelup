package me.z1haze.levelup.discord;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.LevelUpPlayer;
import me.z1haze.levelup.config.ConfigDiscord;
import me.z1haze.levelup.utils.PermissionUtils;
import me.z1haze.levelup.utils.StringUtils;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.group.Group;
import net.luckperms.api.model.user.User;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.awt.*;
import java.util.List;
import java.util.*;

public class Discord {
    private final LevelUp instance = LevelUp.getInstance();
    private JDA bot;
    private boolean botReady = false;
    private final ConfigDiscord config;
    private Guild guild;
    private TextChannel chatChannel;
    private TextChannel linkChannel;
    private final Map<String, UUID> linkCodes = new HashMap<>();

    public Discord() {
        config = (ConfigDiscord) instance.getConfigs().get("discord");

        initialize();
    }

    public void initialize() {
        YamlConfiguration config = this.config.get();

        String botToken = config.getString("bot-token");

        if (botToken == null || botToken.isEmpty()) {
            instance.getLogger().severe("No bot token found! Unable to start bot.");
            return;
        }

        EnumSet<GatewayIntent> intents = EnumSet.of(
                GatewayIntent.GUILD_MEMBERS,
                GatewayIntent.GUILD_MESSAGES,
                GatewayIntent.MESSAGE_CONTENT
        );

        try {
            bot = JDABuilder.create(botToken, intents)
                    .disableCache(
                            CacheFlag.ACTIVITY,
                            CacheFlag.VOICE_STATE,
                            CacheFlag.EMOJI,
                            CacheFlag.STICKER,
                            CacheFlag.CLIENT_STATUS,
                            CacheFlag.ONLINE_STATUS,
                            CacheFlag.SCHEDULED_EVENTS
                    )
                    .build();
        } catch (IllegalArgumentException e) {
            instance.getLogger().severe("Invalid bot token! Unable to start bot.");
            return;
        }

        // attach listeners
        bot.addEventListener(new BotListener(this));
    }

    public void link(LevelUpPlayer lplayer, Member guildMember) {
        if (lplayer == null) {
            instance.getLogger().severe("Unable to link player because they are not in the database!");
            return;
        }

        if (guildMember == null) {
            instance.getLogger().severe("Unable to link player " + lplayer.name + " because they are not in the discord!");
            return;
        }

        // update player data
        lplayer.discordLinked = true;
        lplayer.discordUserId = guildMember.getUser().getId();
        lplayer.discordUsername = guildMember.getUser().getName();

        instance.getPlayerDataManager().savePlayerData(lplayer);

        // sync roles with discord
        syncRoles(lplayer);
    }

    public void unlink(LevelUpPlayer lplayer) {
        if (lplayer == null) return;

        lplayer.discordLinked = false;
        instance.getPlayerDataManager().savePlayerData(lplayer);

        Member member = getGuild().getMemberById(lplayer.discordUserId);
        if (member == null) return;

        Map<String, String> groupMapping = config.getGroupMap();
        List<Role> rolesToRemove = new ArrayList<>();

        for (String roleId : groupMapping.values()) {
            Role role = getGuild().getRoleById(roleId);
            rolesToRemove.add(role);
        }

        getGuild().modifyMemberRoles(member, null, rolesToRemove).queue();
    }

    public void syncRoles(LevelUpPlayer lplayer) {
        if (lplayer == null || lplayer.discordLinked == null || !lplayer.discordLinked) return;

        Member member = getGuild().getMemberById(lplayer.discordUserId);
        if (member == null) return;

        LuckPerms luckperms = instance.getPermissionsManager().getLuckPerms();
        User user = luckperms.getUserManager().getUser(lplayer.uuid);
        Group levelGroup = PermissionUtils.getCurrentGroupOnTrackForUser(luckperms, user, "autorank");
        Group staffGroup = PermissionUtils.getCurrentGroupOnTrackForUser(luckperms, user, "staff");
        Map<String, String> groupMapping = config.getGroupMap();
        List<Role> rolesToAdd = new ArrayList<>();
        List<Role> rolesToRemove = new ArrayList<>();

        for (Map.Entry<String, String> entry : groupMapping.entrySet()) {
            String groupName = entry.getKey();
            String roleId = entry.getValue();
            Role role = getGuild().getRoleById(roleId);

            // if the group is their level group or their staff group, they get the role
            if (levelGroup != null && groupName.equals(levelGroup.getName())
                    || staffGroup != null && groupName.equals(staffGroup.getName())) {
                rolesToAdd.add(role);
                continue;
            }

            // otherwise they don't get the role
            rolesToRemove.add(role);
        }

        getGuild().modifyMemberRoles(member, rolesToAdd, rolesToRemove).queue();

        // hack because bots cannot modify the owner's nickname
        if (!member.isOwner()) {
            member.modifyNickname(lplayer.name).queue();
        }
    }

    public String generateLinkCode(Player p) {
        String code = StringUtils.generateRandomString(5);
        linkCodes.put(code, p.getUniqueId());

        return code;
    }

    public void send(Player player, String content, boolean contentInAuthorLine, Color color) {
        if (chatChannel == null || !botReady) return;

        EmbedBuilder builder = new EmbedBuilder()
                .setColor(color)
                .setAuthor(
                        contentInAuthorLine ? content : player.getDisplayName(),
                        null,
                        "https://crafatar.com/avatars/" + player.getUniqueId() + "?overlay=1"
                );

        if (!contentInAuthorLine) {
            builder.setDescription(content);
        }

        chatChannel.sendMessageEmbeds(builder.build()).queue();
    }

    public ConfigDiscord getConfig() {
        return config;
    }

    public boolean getBotReady() {
        return botReady;
    }

    public void setBotReady(boolean botReady) {
        this.botReady = botReady;
    }

    public JDA getBot() {
        return bot;
    }

    public TextChannel getChatChannel() {
        return chatChannel;
    }

    public void setChatChannel(TextChannel channel) {
        chatChannel = channel;
    }

    public TextChannel getLinkChannel() {
        return linkChannel;
    }

    public void setLinkChannel(TextChannel channel) {
        linkChannel = channel;
    }

    public Guild getGuild() {
        return guild;
    }

    public void setGuild(Guild guild) {
        this.guild = guild;
    }

    public Map<String, UUID> getLinkCodes() {
        return linkCodes;
    }
}
