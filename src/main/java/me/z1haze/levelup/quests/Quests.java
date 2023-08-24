package me.z1haze.levelup.quests;

import io.github.battlepass.BattlePlugin;
import io.github.battlepass.registry.quest.QuestRegistry;

public class Quests {
    public Quests() {
        QuestRegistry questRegistry = BattlePlugin.getApi().getQuestRegistry();

        questRegistry.quest(BuildCreatureQuest::new);
    }
}
