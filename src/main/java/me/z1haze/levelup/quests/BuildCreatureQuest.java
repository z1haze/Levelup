package me.z1haze.levelup.quests;

import io.github.battlepass.BattlePlugin;
import net.advancedplugins.bp.impl.actions.containers.ActionContainer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class BuildCreatureQuest extends ActionContainer {
    public BuildCreatureQuest(BattlePlugin battlePlugin) {
        super(battlePlugin);
    }

    @EventHandler(ignoreCancelled = true)
    public void onMobSpawn(CreatureSpawnEvent e) {
        CreatureSpawnEvent.SpawnReason spawnReason = e.getSpawnReason();

        if (spawnReason != CreatureSpawnEvent.SpawnReason.BUILD_IRONGOLEM
                && spawnReason != CreatureSpawnEvent.SpawnReason.BUILD_SNOWMAN
                && spawnReason != CreatureSpawnEvent.SpawnReason.BUILD_WITHER) {
            return;
        }

        LivingEntity entity = e.getEntity();
        Location entityLocation = entity.getLocation();

        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getLocation().distanceSquared(entityLocation) <= Math.pow(5, 2)) {
                executionBuilder("build-creature")
                        .player(p)
                        .root(entity.getType().toString())
                        .progressSingle()
                        .buildAndExecute();
            }
        }
    }
}
