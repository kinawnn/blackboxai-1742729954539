package com.luckyplugins.minigames.manager;

import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerVoteManager {
    private static Map<UUID, String> playerVotes = new HashMap<>();
    private static Map<String, Integer> voteCount = new HashMap<>();

    public static void setPlayerVote(Player player, String mapName) {
        String previousVote = playerVotes.get(player.getUniqueId());
        if (previousVote != null) {
            // Remove previous vote
            voteCount.put(previousVote, voteCount.getOrDefault(previousVote, 1) - 1);
        }
        
        // Add new vote
        playerVotes.put(player.getUniqueId(), mapName);
        voteCount.put(mapName, voteCount.getOrDefault(mapName, 0) + 1);
    }

    public static String getPlayerVote(Player player) {
        return playerVotes.getOrDefault(player.getUniqueId(), null);
    }

    public static int getVoteCount(String mapName) {
        return voteCount.getOrDefault(mapName, 0);
    }

    public static String getMostVotedMap() {
        String mostVotedMap = null;
        int highestVotes = -1;

        for (Map.Entry<String, Integer> entry : voteCount.entrySet()) {
            if (entry.getValue() > highestVotes) {
                highestVotes = entry.getValue();
                mostVotedMap = entry.getKey();
            }
        }

        return mostVotedMap;
    }

    public static void clearPlayerVote(Player player) {
        String previousVote = playerVotes.remove(player.getUniqueId());
        if (previousVote != null) {
            voteCount.put(previousVote, voteCount.getOrDefault(previousVote, 1) - 1);
        }
    }

    public static void clearAllVotes() {
        playerVotes.clear();
        voteCount.clear();
    }

    public static Map<String, Integer> getAllVotes() {
        return new HashMap<>(voteCount);
    }

    public static int getTotalVotes() {
        return playerVotes.size();
    }

    public static boolean hasVoted(Player player) {
        return playerVotes.containsKey(player.getUniqueId());
    }
}