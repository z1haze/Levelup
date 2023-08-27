package me.z1haze.levelup.listeners;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.events.ConfigChangeEvent;
import me.z1haze.levelup.support.ProtocolLibSupport;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.z1haze.levelup.utils.ChatUtils.colorize;

public class ResourcePackListener implements Listener {
    private final LevelUp instance = LevelUp.getInstance();
    private final Map<UUID, PlayerData> playersInLimbo = new HashMap<>();
    private final Scoreboard scoreboard;
    private final Team teamLimbo;
    private Location limbo;

    public ResourcePackListener() {
        limbo = instance.getConfig("config").get().getLocation("limbo");
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        teamLimbo = scoreboard.registerNewTeam("hide_nametag");
        teamLimbo.setNameTagVisibility(NameTagVisibility.NEVER);

        ProtocolLibSupport.protocolManager.addPacketListener(new PacketAdapter(instance,
                PacketType.Play.Server.CHAT,
                PacketType.Play.Server.SYSTEM_CHAT,
                PacketType.Play.Server.SET_TITLE_TEXT,
                PacketType.Play.Server.SET_SUBTITLE_TEXT
        ) {
            @Override
            public void onPacketSending(PacketEvent e) {
                if (playersInLimbo.containsKey(e.getPlayer().getUniqueId())) {
                    e.setCancelled(true);
                }
            }
        });
    }

    @EventHandler
    public void onConfigChange(ConfigChangeEvent e) {
        limbo = instance.getConfig("config").get().getLocation("limbo");
    }

    @EventHandler(ignoreCancelled = true)
    public void onPlayerJoin(PlayerJoinEvent e) {
        if (limbo == null) return;

        e.setJoinMessage(null);
        limboPlayer(e.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        if (limbo == null) return;

        e.setQuitMessage(null);
        restorePlayer(e.getPlayer());

        if (!playersInLimbo.containsKey(e.getPlayer().getUniqueId())) {
            instance.getServer().broadcastMessage(
                    colorize(instance.getMessage("quit").replace("%player%", e.getPlayer().getDisplayName()))
            );
        }
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent e) {
        if (limbo == null) return;

        PlayerResourcePackStatusEvent.Status status = e.getStatus();
        Player p = e.getPlayer();

        // restore them after an action has been taken
        if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED
                || status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD
                || status == PlayerResourcePackStatusEvent.Status.DECLINED) {
            if (playersInLimbo.containsKey(p.getUniqueId())) {
                restorePlayer(p);
            }
        }

        // broadcast the join message if they successfully loaded the resource pack
        if (status == PlayerResourcePackStatusEvent.Status.SUCCESSFULLY_LOADED) {
            instance.getServer().broadcastMessage(colorize(instance.getMessage("join").replace("%player%", p.getDisplayName())));
        } else if (status == PlayerResourcePackStatusEvent.Status.FAILED_DOWNLOAD) {
            p.kickPlayer("Failed to download resource pack. Please try again.");
        }
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();

        if (playersInLimbo.containsKey(p.getUniqueId())) {
            e.setDeathMessage(null);
            playersInLimbo.remove(p.getUniqueId());
        }
    }

    private void hidePlayerNametag(Player p) {
        teamLimbo.addPlayer(p);
        p.setScoreboard(scoreboard);
    }

    private void showPlayerNametag(Player p) {
        teamLimbo.removePlayer(p);
        p.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
    }

    private void limboPlayer(Player p) {
        PlayerData data = new PlayerData(p);

        // put them in the limbo list
        playersInLimbo.put(p.getUniqueId(), data);

        // hide their nametag
        hidePlayerNametag(p);

        // respawn them if they're dead, so we can teleport them
        if (data.dead) p.spigot().respawn();

        // make them a spectator to hide their ui
        p.setGameMode(GameMode.SPECTATOR);

        // make them invisible so other players who are in limbo cannot see them
        p.setInvisible(true);

        // teleport to limbo
        p.teleport(limbo);
    }

    private void restorePlayer(Player p) {
        if (teamLimbo.hasPlayer(p)) showPlayerNametag(p);
        if (!playersInLimbo.containsKey(p.getUniqueId())) return;

        PlayerData data = playersInLimbo.get(p.getUniqueId());

        // revert gamemode
        if (data.gameMode == GameMode.SPECTATOR) {
            p.setGameMode(GameMode.SURVIVAL); // default them to survival if they were in spectator
        } else {
            p.setGameMode(data.gameMode);
        }

        // make them visible
        p.setInvisible(false);

        // teleport them out of limbo
        if (data.location.distanceSquared(instance.getConfig("config").get().getLocation("limbo")) <= Math.pow(2, 2)) {
            p.teleport(instance.getServer().getWorld("world").getSpawnLocation());
        } else {
            p.teleport(data.location);
        }

        // show their nametag again
        showPlayerNametag(p);

        // if they were dead, kill them again
        if (data.dead) {
            p.setHealth(0);
        } else {
            // remove them from the limbo list
            playersInLimbo.remove(p.getUniqueId());
        }
    }

    private static class PlayerData {
        public Location location;
        public boolean dead;
        public GameMode gameMode;

        public PlayerData(Player p) {
            this.location = p.getLocation();
            this.dead = p.isDead();
            this.gameMode = p.getGameMode();
        }
    }
}
