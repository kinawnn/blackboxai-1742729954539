package me.kinawn.lnplugins.luckydropper.listeners;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import me.kinawn.lnplugins.luckydropper.game.Game;
import me.kinawn.lnplugins.luckydropper.game.GameManager;
import me.kinawn.lnplugins.luckydropper.game.player.GamePlayer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class GameListener implements Listener {
    private final LuckyDropper plugin;
    private final GameManager gameManager;

    public GameListener(LuckyDropper plugin) {
        this.plugin = plugin;
        this.gameManager = plugin.getGameManager();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = gameManager.getGamePlayer(player);
        
        if (gamePlayer == null) return;
        
        Location to = event.getTo();
        Location from = event.getFrom();
        
        // Only check if Y coordinate changed (vertical movement)
        if (to.getY() == from.getY()) return;

        // Check for water landing (checkpoint)
        if (to.getBlock().getType() == Material.WATER || to.getBlock().getType() == Material.STATIONARY_WATER) {
            gamePlayer.setCheckpoint(to);
        }
        
        // Check for finish (configurable material)
        Material finishMaterial = Material.valueOf(plugin.getConfigManager().getString("settings.finish_material"));
        if (to.getBlock().getType() == finishMaterial) {
            Game game = gamePlayer.getGame();
            game.playerFinished(gamePlayer);
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        
        Player player = (Player) event.getEntity();
        GamePlayer gamePlayer = gameManager.getGamePlayer(player);
        
        if (gamePlayer == null) return;

        // Cancel fall damage in game
        if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
            event.setCancelled(true);
            return;
        }

        // Handle death
        if (player.getHealth() - event.getFinalDamage() <= 0) {
            event.setCancelled(true);
            handlePlayerDeath(gamePlayer);
        }
    }

    private void handlePlayerDeath(GamePlayer gamePlayer) {
        gamePlayer.incrementDeaths();
        
        if (gamePlayer.hasCheckpoint()) {
            gamePlayer.teleportToCheckpoint();
        } else {
            // Teleport to spawn if no checkpoint
            Game game = gamePlayer.getGame();
            Location spawnLocation = plugin.getConfigManager().getMapConfig()
                .getSpawnPoint(game.getMapName());
            gamePlayer.getPlayer().teleport(spawnLocation);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        GamePlayer gamePlayer = gameManager.getGamePlayer(player);
        
        if (gamePlayer != null) {
            gameManager.removePlayerFromGame(player);
        }
    }
}