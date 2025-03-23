package com.luckyplugins.minigames.manager;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import java.util.HashSet;
import java.util.Set;

public class SchemaManager {
    private static Set<SchematicArea> occupiedAreas = new HashSet<>();
    private static final int SCHEMATIC_SPACING = 16; // Minimum space between schematics

    public static void initialize() {
        loadExistingSchematics();
    }

    public static Location findNextAvailableLocation(World world, Vector schematicSize) {
        Location baseLocation = world.getSpawnLocation();
        int x = 0;
        int z = 0;
        
        // Spiral pattern search for available space
        int dx = 0;
        int dz = -1;
        int maxAttempts = 100; // Prevent infinite loops
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;
            
            Location testLoc = baseLocation.clone().add(x * SCHEMATIC_SPACING, 0, z * SCHEMATIC_SPACING);
            if (isAreaAvailable(testLoc, schematicSize)) {
                return testLoc;
            }

            // Spiral pattern movement
            if (x == z || (x < 0 && x == -z) || (x > 0 && x == 1-z)) {
                int temp = dx;
                dx = -dz;
                dz = temp;
            }
            x += dx;
            z += dz;
        }

        return null; // No available space found
    }

    private static boolean isAreaAvailable(Location location, Vector size) {
        SchematicArea newArea = new SchematicArea(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockZ(),
            location.getBlockX() + size.getBlockX() + SCHEMATIC_SPACING,
            location.getBlockZ() + size.getBlockZ() + SCHEMATIC_SPACING
        );

        for (SchematicArea area : occupiedAreas) {
            if (area.overlaps(newArea)) {
                return false;
            }
        }

        return true;
    }

    public static void markAreaOccupied(Location location, Vector size) {
        SchematicArea area = new SchematicArea(
            location.getWorld(),
            location.getBlockX(),
            location.getBlockZ(),
            location.getBlockX() + size.getBlockX() + SCHEMATIC_SPACING,
            location.getBlockZ() + size.getBlockZ() + SCHEMATIC_SPACING
        );
        occupiedAreas.add(area);
    }

    public static void loadSchematic(String schematicName, Location location, Player player) {
        // Logic to load schematic using WorldEdit API at the specified location
        // This would use the WorldEdit API to actually load the schematic
    }

    private static void loadExistingSchematics() {
        // Load and register already placed schematics from a configuration
        // This would prevent overlapping on server restart
    }

    private static class SchematicArea {
        private final World world;
        private final int minX, minZ, maxX, maxZ;

        public SchematicArea(World world, int minX, int minZ, int maxX, int maxZ) {
            this.world = world;
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
        }

        public boolean overlaps(SchematicArea other) {
            if (!this.world.equals(other.world)) return false;
            return !(this.maxX < other.minX || this.minX > other.maxX ||
                    this.maxZ < other.minZ || this.minZ > other.maxZ);
        }
    }

    public static void cleanup() {
        occupiedAreas.clear();
    }
}