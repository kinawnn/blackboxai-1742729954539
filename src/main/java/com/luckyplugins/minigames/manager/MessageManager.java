package com.luckyplugins.minigames.manager;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import com.luckyplugins.minigames.LuckyDropperPlugin;

import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private static LuckyDropperPlugin plugin;
    private static FileConfiguration messages;

    public static void initialize(LuckyDropperPlugin main) {
        plugin = main;
        messages = plugin.getConfig(); // Load messages from the config
    }

    public static void loadMessages() {
        // Load messages from messages.yml
        messages = plugin.getConfig();
    }

    public static String getMessage(String key, Map<String, String> placeholders) {
        String message = messages.getString(key, "Message not found.");
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            message = message.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}