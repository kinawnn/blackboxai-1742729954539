package com.luckyplugins.minigames.manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class RewardManager {
    public static void awardRewards(Player player, int position) {
        String command = "";
        switch (position) {
            case 1:
                command = ArenaManager.getRewardCommand("first");
                break;
            case 2:
                command = ArenaManager.getRewardCommand("second");
                break;
            case 3:
                command = ArenaManager.getRewardCommand("third");
                break;
            default:
                command = ArenaManager.getRewardCommand("others");
                break;
        }
        command = command.replace("%player%", player.getName());
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
    }
}