package me.z1haze.levelup.quests;

import io.github.battlepass.BattlePlugin;
import net.advancedplugins.bp.impl.actions.ActionRegistry;

public class Quests {
    public Quests() {
        ActionRegistry actionRegistry = BattlePlugin.getApi().getActionRegistry();

        actionRegistry.quest(instance -> new BlockBreakSilk((BattlePlugin) instance));
        actionRegistry.quest(instance -> new BuildCreatureQuest((BattlePlugin) instance));
    }
}
