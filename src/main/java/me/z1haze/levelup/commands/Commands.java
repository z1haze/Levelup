package me.z1haze.levelup.commands;

import cloud.commandframework.Command;
import cloud.commandframework.CommandTree;
import cloud.commandframework.bukkit.BukkitCommandManager;
import cloud.commandframework.exceptions.NoPermissionException;
import cloud.commandframework.execution.AsynchronousCommandExecutionCoordinator;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.execution.FilteringCommandSuggestionProcessor;
import cloud.commandframework.meta.CommandMeta;
import me.z1haze.levelup.LevelUp;
import me.z1haze.levelup.config.Configurator;
import me.z1haze.levelup.events.ConfigChangeEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.function.Function;

import static me.z1haze.levelup.utils.ChatUtils.sendColorized;

public class Commands {

    private final LevelUp instance = LevelUp.getInstance();
    private BukkitCommandManager<CommandSender> manager;

    public Commands() {
        final Function<CommandTree<CommandSender>, CommandExecutionCoordinator<CommandSender>> executionCoordinatorFunction =
                AsynchronousCommandExecutionCoordinator.<CommandSender>builder().build();

        try {
            manager = new BukkitCommandManager<>(
                    instance,
                    executionCoordinatorFunction,
                    Function.identity(),
                    Function.identity()
            );
        } catch (Exception e) {
            instance.getLogger().severe("Failed to initialize the command this.manager");
            instance.getServer().getPluginManager().disablePlugin(instance);
        }

        // filter suggestions while typing
        manager.commandSuggestionProcessor(new FilteringCommandSuggestionProcessor<>(
                FilteringCommandSuggestionProcessor.Filter.<CommandSender>contains(true).andTrimBeforeLastSpace()
        ));

        // no permission handler
        manager.registerExceptionHandler(NoPermissionException.class, (sender, exception) -> {
            sendColorized(sender, instance.getConfig().getString("messages.no-permission"));
        });
    }

    public void registerCommands() {
        Command.Builder<CommandSender> builderLevelUp = manager.commandBuilder("levelup", "lu")
                .meta(CommandMeta.DESCRIPTION, "Access to LevelUp commands")
                .permission("levelup.command")
                .handler(ctx -> {
                    // TODO: integrate the gui plugin into this command
                });

        manager.command(builderLevelUp);

        // levelup version
        manager.command(
                builderLevelUp.literal("version")
                        .permission("levelup.command.version")
                        .handler(ctx ->
                                instance.getServer().getScheduler().runTask(instance, () ->
                                        sendColorized(ctx.getSender(), "&bLevel&fUp &fv" + instance.getDescription().getVersion() + " &7| by " + instance.getDescription().getAuthors().get(0))))
        );

        // levelup reload
        manager.command(
                builderLevelUp.literal("reload")
                        .permission("levelup.command.reload")
                        .handler(ctx -> {
                            instance.getServer().getScheduler().runTask(instance, () -> {
                                instance.getConfigs().values().forEach(Configurator::load);
                                sendColorized(ctx.getSender(), instance.getMessage("prefix") + " " + instance.getMessage("reloaded"));
                                instance.getServer().getPluginManager().callEvent(new ConfigChangeEvent());
                            });
                        })
        );

        // /trash command
        manager.command(
                manager.commandBuilder("trash", "disposal")
                        .senderType(Player.class)
                        .meta(CommandMeta.DESCRIPTION, "Open your trash can")
                        .permission("levelup.command.trash")
                        .handler(ctx -> {
                            Player player = (Player) ctx.getSender();

                            instance.getServer().getScheduler().runTask(instance, () -> {
                                sendColorized(player, instance.getMessage("prefix") + " " + instance.getMessage("disposal"));
                                Inventory inv = instance.getServer().createInventory(player, 27, "Disposal");
                                player.openInventory(inv);
                            });
                        })
        );

        // register /fly commands
        new FlyCommand(manager);

        // register /discord commands
        new DiscordCommand(manager);
    }
}