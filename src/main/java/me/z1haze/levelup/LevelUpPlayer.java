package me.z1haze.levelup;

import org.bukkit.entity.Player;

import java.util.UUID;

public class LevelUpPlayer {
    public UUID uuid;
    public int id;
    public String name;
    public String level = "visitor";
    public Boolean discordLinked = false;
    public String discordUserId;
    public String discordUsername;

    public LevelUpPlayer(Player p, int id) {
        this.uuid = p.getUniqueId();
        this.id = id;
        this.name = p.getName();
    }
}
