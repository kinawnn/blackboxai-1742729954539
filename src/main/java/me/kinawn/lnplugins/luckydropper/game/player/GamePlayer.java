package me.kinawn.lnplugins.luckydropper.game.player;

import me.kinawn.lnplugins.luckydropper.game.Game;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class GamePlayer {
    private final Player player;
    private final Game game;
    private int score;
    private int deaths;
    private int finalPosition;
    private Location lastCheckpoint;
    private ItemStack[] savedInventory;
    private GameMode savedGameMode;
    private Location savedLocation;

    public GamePlayer(Player player, Game game) {
        this.player = player;
        this.game = game;
        this.score = 0;
        this.deaths = 0;
        this.finalPosition = -1;
        savePlayerState();
    }

    private void savePlayerState() {
        savedInventory = player.getInventory().getContents();
        savedGameMode = player.getGameMode();
        savedLocation = player.getLocation();
        player.getInventory().clear();
        player.setGameMode(GameMode.ADVENTURE);
    }

    public void restorePlayerState() {
        player.getInventory().setContents(savedInventory);
        player.setGameMode(savedGameMode);
        player.teleport(savedLocation);
    }

    public void setCheckpoint(Location location) {
        this.lastCheckpoint = location;
        sendMessage(ChatColor.GREEN + "Checkpoint set!");
    }

    public void teleportToCheckpoint() {
        if (lastCheckpoint != null) {
            player.teleport(lastCheckpoint);
        }
    }

    public void addScore(int amount) {
        this.score += amount;
    }

    public void incrementDeaths() {
        this.deaths++;
    }

    public void returnToLobby() {
        restorePlayerState();
    }

    public void sendMessage(String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public void reset() {
        score = 0;
        deaths = 0;
        finalPosition = -1;
        lastCheckpoint = null;
    }

    // Getters and Setters
    public Player getPlayer() {
        return player;
    }

    public String getName() {
        return player.getName();
    }

    public Game getGame() {
        return game;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getDeaths() {
        return deaths;
    }

    public int getFinalPosition() {
        return finalPosition;
    }

    public void setFinalPosition(int finalPosition) {
        this.finalPosition = finalPosition;
    }

    public Location getLastCheckpoint() {
        return lastCheckpoint;
    }

    public boolean hasCheckpoint() {
        return lastCheckpoint != null;
    }
}