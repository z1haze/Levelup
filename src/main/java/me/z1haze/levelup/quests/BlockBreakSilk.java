package me.z1haze.levelup.quests;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.quests.QuestExecutor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;

public class BlockBreakSilk extends QuestExecutor {
    public BlockBreakSilk(BattlePlugin battlePlugin) {
        super(battlePlugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e) {
        if (e.getPlayer().getInventory().getItemInMainHand().containsEnchantment(Enchantment.SILK_TOUCH)) {
            executionBuilder("block-break-silk")
                    .player(e.getPlayer())
                    .root(e.getBlock().getType().toString())
                    .progressSingle()
                    .buildAndExecute();
        }
    }
}
