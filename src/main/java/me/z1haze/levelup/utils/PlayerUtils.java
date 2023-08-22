package me.z1haze.levelup.utils;

import net.advancedplugins.ae.api.AEAPI;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;

public class PlayerUtils {

    public static Boolean isVanished(Player p) {
        for (MetadataValue meta : p.getMetadata("vanished")) {
            if (meta.asBoolean()) return true;
        }

        return false;
    }

    public static Boolean playerHasWings(Player p) {
        ItemStack boots = p.getInventory().getBoots();

        return boots != null && AEAPI.hasCustomEnchant("wings", boots);
    }
}
