package me.kinawn.lnplugins.luckydropper.commands;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import me.kinawn.lnplugins.luckydropper.game.Game;
import me.kinawn.lnplugins.luckydropper.game.GameManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DropperCommand implements CommandExecutor {
    private final LuckyDropper plugin;
    private final GameManager gameManager;

    public DropperCommand(LuckyDropper plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be used by players!");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        switch (args[0].toLowerCase()) {
            case "join":
                handleJoin(player);
                break;
            case "leave":
                handleLeave(player);
                break;
            case "vote":
                handleVote(player, args);
                break;
            case "stats":
                handleStats(player);
                break;
            default:
                sendHelp(player);
                break;
        }

        return true;
    }

    private void handleJoin(Player player) {
        if (!player.hasPermission("luckydropper.play")) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.no_permission"));
            return;
        }

        if (gameManager.isPlayerInGame(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("game.join.already_in_game"));
            return;
        }

        Game game = gameManager.findAvailableGame();
        if (game == null) {
            // Create new game if no available games
            game = gameManager.createGame(plugin.getConfigManager().getString("settings.default_map"));
        }

        if (game.isFull()) {
            player.sendMessage(plugin.getConfigManager().getMessage("game.join.game_full"));
            return;
        }

        gameManager.addPlayerToGame(player, game);
        player.sendMessage(plugin.getConfigManager().getMessage("game.join.success"));
    }

    private void handleLeave(Player player) {
        if (!gameManager.isPlayerInGame(player)) {
            player.sendMessage(plugin.getConfigManager().getMessage("game.leave.not_in_game"));
            return;
        }

        gameManager.removePlayerFromGame(player);
        player.sendMessage(plugin.getConfigManager().getMessage("game.leave.success"));
    }

    private void handleVote(Player player, String[] args) {
        if (!player.hasPermission("luckydropper.vote")) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.no_permission"));
            return;
        }

        if (args.length == 1) {
            // Open voting menu
            plugin.getVotingManager().openVotingMenu(player);
            return;
        }

        // Handle direct map vote
        String mapName = args[1];
        plugin.getVotingManager().castVote(player, mapName);
    }

    private void handleStats(Player player) {
        if (!player.hasPermission("luckydropper.stats")) {
            player.sendMessage(plugin.getConfigManager().getMessage("general.no_permission"));
            return;
        }

        // Show player stats
        player.sendMessage(plugin.getConfigManager().getMessage("stats.header"));
        // Add stats display logic here
        player.sendMessage(plugin.getConfigManager().getMessage("stats.footer"));
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GRAY + "=== " + ChatColor.AQUA + "LuckyDropper Help" + ChatColor.GRAY + " ===");
        player.sendMessage(ChatColor.YELLOW + "/dropper join" + ChatColor.GRAY + " - Join a game");
        player.sendMessage(ChatColor.YELLOW + "/dropper leave" + ChatColor.GRAY + " - Leave current game");
        player.sendMessage(ChatColor.YELLOW + "/dropper vote [map]" + ChatColor.GRAY + " - Vote for a map");
        player.sendMessage(ChatColor.YELLOW + "/dropper stats" + ChatColor.GRAY + " - View your stats");
        player.sendMessage(ChatColor.GRAY + "===========================");
    }
}