package me.kinawn.lnplugins.luckydropper.listeners;

import me.kinawn.lnplugins.luckydropper.LuckyDropper;
import me.kinawn.lnplugins.luckydropper.voting.VotingManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class VotingListener implements Listener {
    private final LuckyDropper plugin;
    private final VotingManager votingManager;

    public VotingListener(LuckyDropper plugin) {
        this.plugin = plugin;
        this.votingManager = plugin.getVotingManager();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!event.getView().getTitle().equals("Vote for a Map")) return;
        
        event.setCancelled(true);
        
        if (!(event.getWhoClicked() instanceof Player)) return;
        
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || !clickedItem.hasItemMeta() || !clickedItem.getItemMeta().hasDisplayName()) return;

        String mapName = ChatColor.stripColor(clickedItem.getItemMeta().getDisplayName());
        
        // Handle voting
        if (votingManager.isVotingActive()) {
            votingManager.castVote(player, mapName);
            player.closeInventory();
        } else {
            player.sendMessage(plugin.getConfigManager().getMessage("vote.not_active"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || !item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) return;

        String votingItemName = plugin.getConfigManager().getString("vote.items.voting.name");
        String leaveItemName = plugin.getConfigManager().getString("vote.items.leave.name");
        
        String itemName = ChatColor.stripColor(item.getItemMeta().getDisplayName());

        if (itemName.equals(ChatColor.stripColor(votingItemName))) {
            event.setCancelled(true);
            if (votingManager.isVotingActive()) {
                votingManager.openVotingMenu(player);
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("vote.not_active"));
            }
        } else if (itemName.equals(ChatColor.stripColor(leaveItemName))) {
            event.setCancelled(true);
            plugin.getGameManager().removePlayerFromGame(player);
        }
    }

    @EventHandler
    public void onVotingItemClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item == null || item.getType() != Material.valueOf(plugin.getConfigManager().getString("vote.items.voting.material"))) {
            return;
        }

        if (!item.hasItemMeta() || !item.getItemMeta().hasDisplayName()) {
            return;
        }

        String votingItemName = plugin.getConfigManager().getString("vote.items.voting.name");
        if (ChatColor.stripColor(item.getItemMeta().getDisplayName()).equals(ChatColor.stripColor(votingItemName))) {
            event.setCancelled(true);
            if (votingManager.isVotingActive()) {
                votingManager.openVotingMenu(player);
            } else {
                player.sendMessage(plugin.getConfigManager().getMessage("vote.not_active"));
            }
        }
    }
}