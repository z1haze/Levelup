package me.z1haze.levelup.quests;

import net.advancedplugins.bp.impl.actions.containers.ActionContainer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class BlockBreakSilkQuest extends ActionContainer {
    public BlockBreakSilkQuest(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            executionBuilder("block-break-silk")
                    .player(e.getPlayer())
                    .root(e.getBlock().getType().toString().toLowerCase())
                    .progressSingle()
                    .buildAndExecute();
        }
    }
}
