package me.z1haze.levelup.commands;

import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cloud.commandframework.meta.CommandMeta;
import com.griefdefender.api.GriefDefender;
import com.griefdefender.api.claim.Claim;
import me.z1haze.levelup.LevelUp;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import static me.z1haze.levelup.utils.ChatUtils.sendColorized;
import static me.z1haze.levelup.utils.PlayerUtils.playerHasWings;

public class FlyCommand {
    private final LevelUp instance = LevelUp.getInstance();

    public FlyCommand(BukkitCommandManager<CommandSender> commandManager) {
        // /fly
        commandManager.command(
                commandManager.commandBuilder("fly")
                        .meta(CommandMeta.DESCRIPTION, "Toggle your flight")
                        .senderType(Player.class)
                        .permission("levelup.command.fly")
                        .handler(ctx -> {
                            Player sender = (Player) ctx.getSender();
                            Claim claim = GriefDefender.getCore().getClaimAt(sender.getLocation());

                            // if a player has wings they can fly in the wilderness

                            if ((claim == null || claim.isWilderness()) && !playerHasWings(sender)) {
                                sendColorized(sender, instance.getMessage("prefix") + " " + instance.getMessage("flight.not-allowed"));
                            } else if (toggleFlight(sender)) {
                                sendColorized(sender, instance.getMessage("prefix") + " " + instance.getMessage("flight.enabled"));
                            } else {
                                sendColorized(sender, instance.getMessage("prefix") + " " + instance.getMessage("flight.disabled"));
                            }
                        })
        );

        // /fly <player>
        commandManager.command(
                commandManager.commandBuilder("fly")
                        .meta(CommandMeta.DESCRIPTION, "Toggle another player's flight")
                        .permission("levelup.command.fly.others")
                        .argument(PlayerArgument.of("player"))
                        .handler(ctx -> {
                            Player player = ctx.get("player");
                            Claim claim = GriefDefender.getCore().getClaimAt(player.getLocation());

                            if ((claim == null || claim.isWilderness()) && !playerHasWings(player)) {
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("flight.not-allowed-other"));
                            } else if (toggleFlight(player)) {
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("flight.enabled-other").replace("%player%", player.getDisplayName()));
                                sendColorized(player, instance.getMessage("prefix") + " " + instance.getMessage("flight.enabled"));
                            } else {
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("flight.disabled-other").replace("%player%", player.getDisplayName()));
                                sendColorized(player, instance.getMessage("prefix") + " " + instance.getMessage("flight.disabled"));
                            }
                        })
        );
    }

    private boolean toggleFlight(Player player) {
        if (player.getAllowFlight()) {
            player.setAllowFlight(false);
            player.setFlying(false);
            return false;
        } else {
            player.setAllowFlight(true);
            return true;
        }
    }
}