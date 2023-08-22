package me.z1haze.levelup.listeners;

import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.event.BorderClaimEvent;
import com.griefdefender.api.event.Event;
import com.griefdefender.lib.kyori.event.EventBus;
import me.z1haze.levelup.LevelUp;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static me.z1haze.levelup.utils.ChatUtils.sendColorized;
import static me.z1haze.levelup.utils.PlayerUtils.isVanished;
import static me.z1haze.levelup.utils.PlayerUtils.playerHasWings;

public class PlayerFlightListener implements Listener {
    private final LevelUp instance = LevelUp.getInstance();
    public final Map<UUID, Long> playerFlightMap = new HashMap<>();

    public PlayerFlightListener() {
        EventBus<Event> eventBus = GriefDefender.getEventManager().getBus();

        /*
         * Stop flight for players entering the wilderness if they don't have wings
         */
        eventBus.subscribe(BorderClaimEvent.class, e -> {
            Player p = instance.getServer().getPlayer(e.getEntityUniqueId());

            if (p == null || isVanished(p) || p.getGameMode() != org.bukkit.GameMode.SURVIVAL) return;
            if (playerHasWings(p)) return;

            // entering wilderness
            if (e.getEnterClaim().isWilderness()) {
                if (p.isFlying()) {
                    playerFlightMap.put(p.getUniqueId(), System.currentTimeMillis());
                }

                stopFlight(p, p.isFlying());
            }
        });
    }

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e) {
        playerFlightMap.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent e) {
        if (e.getCause() != EntityDamageEvent.DamageCause.FALL || !(e.getEntity() instanceof Player p)) {
            return;
        }

        // if we're tracking their flight, cancel fall damage
        if (playerFlightMap.containsKey(p.getUniqueId())) {
            playerFlightMap.remove(p.getUniqueId());
            e.setCancelled(true);
        }
    }

    private void stopFlight(Player p, Boolean notify) {
        p.setAllowFlight(false);
        p.setFlying(false);

        if (notify) {
            sendColorized(p, instance.getMessage("prefix") + " " + instance.getMessage("flight.not-allowed"));
        }

        // attempt to remove them from the flight map once they are on the ground to that cannot save up fall damage resistance
        instance.getServer().getScheduler().runTaskTimer(instance, task -> {
            if (p.isOnGround() || !p.isOnline() || !playerFlightMap.containsKey(p.getUniqueId())) {
                playerFlightMap.remove(p.getUniqueId());
                task.cancel();
            }
        }, 10L, 10L);
    }
}
