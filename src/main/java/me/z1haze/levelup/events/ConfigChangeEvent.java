package me.z1haze.levelup.events;

import me.z1haze.levelup.LevelUp;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class ConfigChangeEvent extends Event {
    public static final HandlerList HANDLERS = new HandlerList();
    public static final LevelUp instance = LevelUp.getInstance();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() { return HANDLERS; }
}