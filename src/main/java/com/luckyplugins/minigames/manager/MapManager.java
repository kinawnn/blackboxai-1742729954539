package com.luckyplugins.minigames.manager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MapManager {
    private static FileConfiguration mapConfig;
    private static Map<UUID, String> playerCurrentMap = new HashMap<>();
    private static Map<String, MapData> maps = new HashMap<>();
    private static Map<String, SchematicData> schematics = new HashMap<>();

    public static void initialize() {
        loadMapConfig();
        loadMaps();
    }

    private static void loadMapConfig() {
        File configFile = new File(Bukkit.getPluginManager().getPlugin("LuckyDropper").getDataFolder(), "map.yml");
        mapConfig = YamlConfiguration.loadConfiguration(configFile);
    }

    private static void loadMaps() {
        Set<String> mapKeys = mapConfig.getConfigurationSection("maps").getKeys(false);
        for (String key : mapKeys) {
            String world = mapConfig.getString("maps." + key + ".world");
            int[] pos1 = mapConfig.getIntList("maps." + key + ".pos1").stream().mapToInt(i -> i).toArray();
            int[] pos2 = mapConfig.getIntList("maps." + key + ".pos2").stream().mapToInt(i -> i).toArray();
            boolean enabled = mapConfig.getBoolean("maps." + key + ".enabled");
            
            maps.put(key, new MapData(world, pos1, pos2, enabled));
            
            // Load associated schematic data
            String schematicName = mapConfig.getString("maps." + key + ".schematic");
            if (schematicName != null) {
                schematics.put(key, new SchematicData(schematicName));
            }
        }
    }

    public static String getCurrentMap(Player player) {
        return playerCurrentMap.getOrDefault(player.getUniqueId(), "None");
    }

    public static void setPlayerMap(Player player, String mapName) {
        if (maps.containsKey(mapName)) {
            playerCurrentMap.put(player.getUniqueId(), mapName);
        }
    }

    public static boolean isMapEnabled(String mapName) {
        MapData mapData = maps.get(mapName);
        return mapData != null && mapData.isEnabled();
    }

    public static Set<String> getAvailableMaps() {
        return maps.keySet();
    }

    public static boolean loadMapSchematic(String mapName) {
        SchematicData schematicData = schematics.get(mapName);
        if (schematicData != null) {
            // Find next available location
            MapData mapData = maps.get(mapName);
            if (mapData != null) {
                return SchemaManager.loadSchematic(schematicData.getSchematicName(), mapData);
            }
        }
        return false;
    }

    public static void cleanup() {
        playerCurrentMap.clear();
        maps.clear();
        schematics.clear();
    }

    private static class MapData {
        private final String world;
        private final int[] pos1;
        private final int[] pos2;
        private final boolean enabled;

        public MapData(String world, int[] pos1, int[] pos2, boolean enabled) {
            this.world = world;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    private static class SchematicData {
        private final String schematicName;

        public SchematicData(String schematicName) {
            this.schematicName = schematicName;
        }

        public String getSchematicName() {
            return schematicName;
        }
    }
}