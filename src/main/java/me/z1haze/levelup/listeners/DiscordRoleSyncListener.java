package me.z1haze.levelup.listeners;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.LevelUpPlayer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.event.EventBus;
import net.luckperms.api.event.user.track.UserDemoteEvent;
import net.luckperms.api.event.user.track.UserPromoteEvent;
import net.luckperms.api.event.user.track.UserTrackEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class DiscordRoleSyncListener implements Listener {
    private final LevelUp instance = LevelUp.getInstance();

    public DiscordRoleSyncListener() {
        LuckPerms luckPerms = instance.getPermissionsManager().getLuckPerms();
        EventBus eventBus = luckPerms.getEventBus();
        eventBus.subscribe(instance, UserPromoteEvent.class, this::onUserPromote);
        eventBus.subscribe(instance, UserDemoteEvent.class, this::onUserDemote);
    }

    private void onUserPromote(UserTrackEvent event) {
        handlePromoteDemote(event);
    }

    private void onUserDemote(UserTrackEvent event) {
        handlePromoteDemote(event);
    }

    private void handlePromoteDemote(UserTrackEvent e) {
        if (!instance.getDiscord().getBotReady()) return;
        LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(e.getUser().getUniqueId());

        if (lplayer != null) {
            e.getGroupTo().ifPresent(group -> {
                lplayer.level = group;
                instance.getPlayerDataManager().savePlayerData(lplayer);
            });

            instance.getDiscord().syncRoles(lplayer);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (!instance.getDiscord().getBotReady()) return;
        LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(e.getPlayer().getUniqueId());

        if (lplayer != null) {
            instance.getDiscord().syncRoles(lplayer);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (!instance.getDiscord().getBotReady()) return;
        LevelUpPlayer lplayer = instance.getPlayerDataManager().getPlayerData(e.getPlayer().getUniqueId());

        if (lplayer != null) {
            instance.getDiscord().syncRoles(lplayer);
        }
    }
}
