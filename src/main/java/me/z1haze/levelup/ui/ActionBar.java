package me.z1haze.levelup.ui;

import me.clip.placeholderapi.PlaceholderAPI;
import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.events.ConfigChangeEvent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Bed;
import org.bukkit.block.data.type.Jukebox;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

import static me.z1haze.levelup.utils.ChatUtils.colorize;

public class ActionBar implements Listener {
    private final LevelUp instance = LevelUp.getInstance();
    private final HashSet<UUID> isPaused = new HashSet<>();
    private final HashMap<UUID, Integer> timer = new HashMap<>();
    private final HashMap<UUID, Integer> currentAction = new HashMap<>();
    private final Map<String, String> charMap = new HashMap<>() {{
        put("0", "ꢛ");
        put("1", "ꢚ");
        put("2", "ꢜ");
        put("3", "ꢝ");
        put("4", "ꢞ");
        put("5", "ꢟ");
        put("6", "ꢠ");
        put("7", "ꢡ");
        put("8", "ꢢ");
        put("9", "ꢣ");
        put(",", "ꢥ");
        put("-1", "\uF801");
        put("+1", "\uF805");
        put("gem", "ꢤ");
    }};

    // 170 pixels up to the gem for right alignment
    // numbers are 6 pixels wide
    // commas are 2 pixels wide
    final int actionBarPadding = 170;

    public void startUpdateActionBar() {
        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, () -> {
            for (Player p : instance.getServer().getOnlinePlayers()) {
                if (!currentAction.containsKey(p.getUniqueId())) {
                    currentAction.put(p.getUniqueId(), 0);
                }

                if (!isPaused.contains(p.getUniqueId())) {

                    String balString = PlaceholderAPI.setPlaceholders(p, "%elementalgems_balance_formatted%");

                    int leftPadding = actionBarPadding;
                    StringBuilder output = new StringBuilder();

                    for (int i = 0; i < balString.length(); i++) {
                        char c = balString.charAt(i);

                        // subtract from the padding the number of pixels each character takes up
                        if (charMap.containsKey(String.valueOf(c))) {
                            leftPadding -= (c == ',') ? 2 : 6;

                            output.append(charMap.get(String.valueOf(c)));
                            output.append(charMap.get("-1"));
                        } else {
                            output.append(c);
                        }
                    }

                    for (int i=0; i<leftPadding; i++) {
                        output.insert(0, charMap.get("+1"));
                    }

                    // append the gem
                    output.append(charMap.get("+1")).append(charMap.get("gem"));

                    sendActionBar(p, "&#4e5c24" + output);
                }
            }
        }, 0L, 20L);

        instance.getServer().getScheduler().scheduleSyncRepeatingTask(instance, () -> {
            for (Player p : instance.getServer().getOnlinePlayers()) {
                Integer time = timer.get(p.getUniqueId());

                if (time != null) {
                    if (time != 0) {
                        timer.put(p.getUniqueId(), time - 1);
                    }
                } else {
                    timer.put(p.getUniqueId(), 0);
                }
            }
        }, 0L, 2L);
    }

    private void sendActionBar(Player player, String message) {
        message = PlaceholderAPI.setPlaceholders(player, message);
        message = colorize(message);

        instance.getProtocolLibSupport().sendNewActionBar(player, message);
    }

    private void resetActionBar(UUID uuid) {
        timer.remove(uuid);
        currentAction.remove(uuid);
        isPaused.remove(uuid);
    }

    private void resetActionBars() {
        timer.clear();
        currentAction.clear();
        isPaused.clear();
    }

    public void setPaused(UUID uuid, int ticks) {
        isPaused.add(uuid);

        Integer action = currentAction.get(uuid);

        if (action != null) {
            currentAction.put(uuid, action + 1);
        } else {
            currentAction.put(uuid, 0);
        }

        int thisAction = this.currentAction.get(uuid);
        new BukkitRunnable() {
            @Override
            public void run() {
                Integer actionBarCurrentAction = currentAction.get(uuid);

                if (actionBarCurrentAction != null) {
                    if (thisAction == actionBarCurrentAction) {
                        isPaused.remove(uuid);
                    }
                }
            }
        }.runTaskLater(instance, ticks);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        this.resetActionBar(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onReload(ConfigChangeEvent e) {
        resetActionBars();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        Player p = e.getPlayer();

        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = e.getClickedBlock();
            if (block == null) return;

            // if they are clicking on a bet, pause so vanilla action bar can show
            if (block.getType().name().contains("_BED")) {
                Location center;
                Block other = block;

                if (block.getBlockData() instanceof Bed bed) {
                    if (bed.getPart() == Bed.Part.FOOT) {
                        other = block.getRelative(bed.getFacing());
                    } else {
                        other = block.getRelative(bed.getFacing().getOppositeFace());
                    }
                }

                // Get the closest block
                Location mainLoc = block.getLocation().add(0.5, 0, 0.5);
                Location otherLoc = other.getLocation().add(0.5, 0, 0.5);

                if (mainLoc.distanceSquared(p.getLocation()) < otherLoc.distanceSquared(p.getLocation())) {
                    center = mainLoc;
                } else {
                    center = otherLoc;
                }

                if (p.getLocation().distanceSquared(center) >= 9) { // If player not is close enough to the bed
                    setPaused(p.getUniqueId(), 40);
                } else {
                    // if night
                    if (p.getWorld().getTime() >= 12541 && p.getWorld().getTime() <= 23458) {
                        for (Entity entity : p.getWorld().getNearbyEntities(center, 8, 5, 8)) {
                            // Check if mob is hostile
                            if (entity instanceof PigZombie pigZombie) {
                                if (pigZombie.isAngry()) {
                                    setPaused(p.getUniqueId(), 40);
                                    break;
                                }
                            } else if (entity instanceof Enderman enderman) {
                                if (enderman.getTarget() != null) {
                                    setPaused(p.getUniqueId(), 40);
                                    break;
                                }
                            } else if (entity instanceof Enemy) {
                                setPaused(p.getUniqueId(), 40);
                                break;
                            }
                        }
                    } else { // if day
                        setPaused(p.getUniqueId(), 40);
                    }
                }
            } else if (block.getType() == Material.JUKEBOX) {
                ItemStack item = e.getItem();

                if (item == null) return;

                if (item.getType().isRecord()) {
                    boolean isPlace = false;

                    if (block.getBlockData() instanceof Jukebox jukebox) {
                        if (!jukebox.hasRecord()) {
                            isPlace = true;
                        }
                    }

                    if (isPlace) {
                        // Pause action bar of any player within 65 blocks
                        for (Entity entity : block.getWorld().getNearbyEntities(block.getLocation(), 65, 65, 65)) {
                            if (entity instanceof Player) {
                                if (entity.getLocation().distanceSquared(block.getLocation()) <= 4225) {
                                    Player listener = (Player) entity;
                                    setPaused(listener.getUniqueId(), 40);
                                }
                            }
                        }
                    }
                }
            } else {
                // pause for height limit message
                ItemStack item = e.getItem();
                if (item == null) return;

                if (item.getType().isBlock()) {
                    if (block.getY() == block.getWorld().getMaxHeight() - 1) {
                        if (e.getBlockFace() == BlockFace.UP) {
                            setPaused(p.getUniqueId(), 40);
                        }
                    }
                }
            }
        }
    }
}
