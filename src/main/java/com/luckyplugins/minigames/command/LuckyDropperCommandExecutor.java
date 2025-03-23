package com.luckyplugins.minigames.command;

import com.luckyplugins.minigames.LuckyDropperPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LuckyDropperCommandExecutor implements CommandExecutor {
    private final LuckyDropperPlugin plugin;

    public LuckyDropperCommandExecutor(LuckyDropperPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(plugin.getConfig().getString("messages.general.usage"));
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join":
                if (sender instanceof Player) {
                    // Logic for joining the active map
                    Player player = (Player) sender;
                    // Join logic here
                } else {
                    sender.sendMessage("Only players can join a map.");
                }
                break;

            case "joinid":
                if (args.length < 2) {
                    sender.sendMessage(plugin.getConfig().getString("messages.general.usage"));
                    return true;
                }
                // Logic for joining a specific map by ID
                break;

            case "vote":
                // Logic for opening the voting UI
                break;

            case "setting":
                if (!sender.hasPermission("luckydropper.admin")) {
                    sender.sendMessage(plugin.getConfig().getString("messages.general.no_permission"));
                    return true;
                }
                // Logic for changing settings
                break;

            case "reload":
                if (!sender.hasPermission("luckydropper.admin")) {
                    sender.sendMessage(plugin.getConfig().getString("messages.general.no_permission"));
                    return true;
                }
                // Logic for reloading configurations
                break;

            default:
                sender.sendMessage(plugin.getConfig().getString("messages.general.usage"));
                break;
        }
        return true;
    }
}