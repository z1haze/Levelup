package me.z1haze.levelup;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class Placeholders extends PlaceholderExpansion {
    private final LevelUp plugin;

    public Placeholders(LevelUp plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public @NotNull String getIdentifier() {
        return "leve1up";
    }

    @Override
    public @NotNull String getAuthor() {
        return "leve1up";
    }

    @Override
    public @NotNull String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        if (params.equalsIgnoreCase("id")) {
            LevelUpPlayer lplayer = plugin.getPlayerDataManager().getPlayerData(p.getUniqueId());

            if (lplayer != null) {
                return String.valueOf(lplayer.id);
            }
        }

        return null;
    }
}