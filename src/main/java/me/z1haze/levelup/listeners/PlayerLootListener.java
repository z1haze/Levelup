package me.z1haze.levelup.listeners;

import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.config.ConfigAE;
import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.loot.Lootable;
import org.bukkit.persistence.PersistentDataType;

public class PlayerLootListener implements Listener {
    private final LevelUp instance = LevelUp.getInstance();
    private final NamespacedKey key = new NamespacedKey(instance, "looted");

    @EventHandler(ignoreCancelled = true)
    public void onPlayerLoot(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();

        if (block != null) {
            removeDisallowedLoot(block);
        }
    }

    private void removeDisallowedLoot(Block block) {
        BlockState blockState = block.getState();

        // lootable containers are we that we want to check
        if (!(blockState instanceof Lootable) || !(blockState instanceof Container c)) return;

        // if this container has already been processed, skip it
        if (containerIsProcessed(c)) return;

        Inventory inventory = ((Container) blockState).getSnapshotInventory();

        for (ItemStack i : inventory.getContents()) {
            if (i == null || i.getType() != Material.ENCHANTED_BOOK) continue;

            ItemMeta meta = i.getItemMeta();

            // meta exists on vanilla enchanted books but not on AE enchanted books
            if (meta instanceof EnchantmentStorageMeta enchantMeta && !enchantMeta.getStoredEnchants().isEmpty()) {
                continue;
            }

            // if we disallow the group of the enchant in loot, remove it
            if (isDisallowedFromLooting(i)) {
                inventory.removeItem(i);
            }
        }

        setContainerProcessedFlag(c);
        c.update();
    }

    private Boolean containerIsProcessed(Container c) {
        return c.getPersistentDataContainer().has(key, PersistentDataType.BOOLEAN);
    }

    private Boolean isDisallowedFromLooting(ItemStack itemStack) {
        return ((ConfigAE) instance.getConfig("ae")).getDisallowedEnchantmentGroupsInLoot().contains(AEAPI.getEnchantGroupFromBook(itemStack));
    }

    private void setContainerProcessedFlag(Container c) {
        c.getPersistentDataContainer().set(key, PersistentDataType.BOOLEAN, true);
    }
}
