package me.z1haze.levelup.quests;

import com.archyx.aureliumskills.api.event.SkillLevelUpEvent;
import com.archyx.aureliumskills.api.event.XpGainEvent;
import net.advancedplugins.bp.impl.actions.containers.ActionContainer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AureliumSkillsQuests extends ActionContainer {
    private final Map<UUID, Map<String, Double>> playerXp = new HashMap<>();

    public AureliumSkillsQuests(JavaPlugin javaPlugin) {
        super(javaPlugin);
    }

    @EventHandler
    public void playerJoined(PlayerJoinEvent e) {
        playerXp.put(e.getPlayer().getUniqueId(), new HashMap<>(Map.ofEntries(
                Map.entry("farming", 0.0),
                Map.entry("foraging", 0.0),
                Map.entry("mining", 0.0),
                Map.entry("fishing", 0.0),
                Map.entry("excavation", 0.0),
                Map.entry("archery", 0.0),
                Map.entry("defense", 0.0),
                Map.entry("fighting", 0.0),
                Map.entry("endurance", 0.0),
                Map.entry("agility", 0.0),
                Map.entry("alchemy", 0.0),
                Map.entry("enchanting", 0.0),
                Map.entry("sorcery", 0.0),
                Map.entry("healing", 0.0),
                Map.entry("forging", 0.0)
        )));
    }


    @EventHandler
    public void playerQuit(PlayerQuitEvent e) {
        playerXp.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler(ignoreCancelled = true)
    public void onLevelUp(SkillLevelUpEvent e) {
        executionBuilder("skills-level-up")
                .player(e.getPlayer())
                .progressSingle()
                .buildAndExecute();
    }

    @EventHandler(ignoreCancelled = true)
    public void onGainXp(XpGainEvent e) {
        String skillName = e.getSkill().name().toLowerCase();
        double amount = e.getAmount();

        // add xp to player
        playerXp.get(e.getPlayer().getUniqueId()).merge(skillName, amount, Double::sum);

        if (playerXp.get(e.getPlayer().getUniqueId()).get(skillName) >= 1) {
            // remove 1 xp from player because we are going to add it to the (potential) quest
            playerXp.get(e.getPlayer().getUniqueId()).merge(skillName, -1d, Double::sum);

            executionBuilder("skills-xp-gain")
                    .player(e.getPlayer())
                    .root(skillName)
                    .progressSingle()
                    .buildAndExecute();
        }
    }
}
