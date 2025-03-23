package me.kinawn.lnplugins.luckydropper.commands;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import me.kinawn.lnplugins.luckydropper.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropperAdminCommand implements CommandExecutor {
    private final LuckyDropper plugin;

    public DropperAdminCommand(LuckyDropper plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("luckydropper.admin")) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.no_permission"));
            return true;
        }

        if (args.length == 0) {
            sendAdminHelp(sender);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "create":
                handleCreate(sender, args);
                break;
            case "delete":
                handleDelete(sender, args);
                break;
            case "setspawn":
                handleSetSpawn(sender, args);
                break;
            case "setcheckpoint":
                handleSetCheckpoint(sender, args);
                break;
            case "setfinish":
                handleSetFinish(sender, args);
                break;
            case "reload":
                handleReload(sender);
                break;
            case "stop":
                handleStop(sender, args);
                break;
            case "list":
                handleList(sender);
                break;
            default:
                sendAdminHelp(sender);
                break;
        }

        return true;
    }

    private void handleCreate(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.player_only"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /dropperadmin create <name>");
            return;
        }

        String mapName = args[1];
        Player player = (Player) sender;
        Location location = player.getLocation();

        try {
            plugin.getConfigManager().getMapConfig().createMap(mapName, location);
            sender.sendMessage(plugin.getConfigManager().getMessage("map.created").replace("{map}", mapName));
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.create.failed").replace("{map}", mapName));
            e.printStackTrace();
        }
    }

    private void handleDelete(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /dropperadmin delete <name>");
            return;
        }

        String mapName = args[1];

        try {
            plugin.getConfigManager().getMapConfig().deleteMap(mapName);
            sender.sendMessage(plugin.getConfigManager().getMessage("map.deleted").replace("{map}", mapName));
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.delete.failed").replace("{map}", mapName));
            e.printStackTrace();
        }
    }

    private void handleSetSpawn(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.player_only"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /dropperadmin setspawn <map>");
            return;
        }

        String mapName = args[1];
        Player player = (Player) sender;
        Location location = player.getLocation();

        try {
            plugin.getConfigManager().getMapConfig().setSpawnPoint(mapName, location);
            sender.sendMessage(plugin.getConfigManager().getMessage("map.set_spawn").replace("{map}", mapName));
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.setspawn.failed").replace("{map}", mapName));
            e.printStackTrace();
        }
    }

    private void handleSetCheckpoint(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.player_only"));
            return;
        }

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Usage: /dropperadmin setcheckpoint <map> <number>");
            return;
        }

        String mapName = args[1];
        int checkpointNumber;
        try {
            checkpointNumber = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "Invalid checkpoint number!");
            return;
        }

        Player player = (Player) sender;
        Location location = player.getLocation();

        try {
            plugin.getConfigManager().getMapConfig().setCheckpoint(mapName, checkpointNumber, location);
            sender.sendMessage(ChatColor.GREEN + "Checkpoint " + checkpointNumber + " set for map " + mapName);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to set checkpoint!");
            e.printStackTrace();
        }
    }

    private void handleSetFinish(CommandSender sender, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.getConfigManager().getMessage("general.player_only"));
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /dropperadmin setfinish <map>");
            return;
        }

        String mapName = args[1];
        Player player = (Player) sender;
        Location location = player.getLocation();

        try {
            plugin.getConfigManager().getMapConfig().setFinishLocation(mapName, location);
            sender.sendMessage(ChatColor.GREEN + "Finish location set for map " + mapName);
        } catch (Exception e) {
            sender.sendMessage(ChatColor.RED + "Failed to set finish location!");
            e.printStackTrace();
        }
    }

    private void handleReload(CommandSender sender) {
        try {
            plugin.getConfigManager().reloadConfigs();
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.reload.success"));
        } catch (Exception e) {
            sender.sendMessage(plugin.getConfigManager().getMessage("admin.reload.failed"));
            e.printStackTrace();
        }
    }

    private void handleStop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            // Stop all games
            plugin.getGameManager().shutdown();
            sender.sendMessage(ChatColor.GREEN + "All games have been stopped!");
            return;
        }

        // Stop specific game
        String gameId = args[1];
        try {
            Game game = plugin.getGameManager().getGame(java.util.UUID.fromString(gameId));
            if (game != null) {
                game.stop();
                sender.sendMessage(ChatColor.GREEN + "Game " + gameId + " has been stopped!");
            } else {
                sender.sendMessage(ChatColor.RED + "Game not found!");
            }
        } catch (IllegalArgumentException e) {
            sender.sendMessage(ChatColor.RED + "Invalid game ID!");
        }
    }

    private void handleList(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "=== " + ChatColor.AQUA + "Active Games" + ChatColor.GRAY + " ===");
        for (Game game : plugin.getGameManager().getGames().values()) {
            sender.sendMessage(ChatColor.YELLOW + "Game " + game.getUniqueId() + ": " + 
                ChatColor.WHITE + game.getPlayerCount() + " players, Map: " + game.getMapName());
        }
        sender.sendMessage(ChatColor.GRAY + "===========================");
    }

    private void sendAdminHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GRAY + "=== " + ChatColor.AQUA + "LuckyDropper Admin Help" + ChatColor.GRAY + " ===");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin create <name>" + ChatColor.GRAY + " - Create a new map");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin delete <name>" + ChatColor.GRAY + " - Delete a map");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin setspawn <map>" + ChatColor.GRAY + " - Set map spawn point");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin setcheckpoint <map> <number>" + ChatColor.GRAY + " - Set checkpoint");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin setfinish <map>" + ChatColor.GRAY + " - Set finish location");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin reload" + ChatColor.GRAY + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin stop [gameId]" + ChatColor.GRAY + " - Stop game(s)");
        sender.sendMessage(ChatColor.YELLOW + "/dropperadmin list" + ChatColor.GRAY + " - List active games");
        sender.sendMessage(ChatColor.GRAY + "===================================");
    }
}