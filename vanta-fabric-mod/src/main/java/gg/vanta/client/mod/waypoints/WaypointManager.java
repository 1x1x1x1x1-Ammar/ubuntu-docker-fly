package gg.vanta.client.mod.waypoints;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WaypointManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, List<Waypoint>> waypointsByServer = new ConcurrentHashMap<>();
    private static Path waypointsDir;
    
    public static class Waypoint {
        public String name;
        public double x, y, z;
        public String dimension;
        public int color = 0xFF00FF87;
        public boolean visible = true;
        
        public Waypoint(String name, double x, double y, double z, String dimension) {
            this.name = name;
            this.x = x;
            this.y = y;
            this.z = z;
            this.dimension = dimension;
        }
        
        // For nether portal conversion
        public Waypoint convertToNether() {
            if (dimension.equals("minecraft:overworld")) {
                return new Waypoint(name, x / 8, y, z / 8, "minecraft:the_nether");
            } else if (dimension.equals("minecraft:the_nether")) {
                return new Waypoint(name, x * 8, y, z * 8, "minecraft:overworld");
            }
            return this;
        }
    }
    
    public static void init() {
        waypointsDir = FabricLoader.getInstance().getGameDir().resolve("vanta/waypoints");
        try {
            Files.createDirectories(waypointsDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadAll();
    }
    
    private static String getServerKey() {
        // Generate a unique key for the current server/world
        return "default_server"; // TODO: Get actual server IP or world seed
    }
    
    public static void loadAll() {
        if (!Files.exists(waypointsDir)) return;
        
        try {
            for (Path file : Files.list(waypointsDir).toList()) {
                if (file.toString().endsWith(".json")) {
                    String serverKey = file.getFileName().toString().replace(".json", "");
                    load(serverKey);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void load(String serverKey) {
        Path file = waypointsDir.resolve(serverKey + ".json");
        if (!Files.exists(file)) return;
        
        try (Reader reader = Files.newBufferedReader(file)) {
            Type listType = new TypeToken<List<Waypoint>>(){}.getType();
            List<Waypoint> waypoints = GSON.fromJson(reader, listType);
            if (waypoints != null) {
                waypointsByServer.put(serverKey, waypoints);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void save(String serverKey) {
        try {
            Files.createDirectories(waypointsDir);
            Path file = waypointsDir.resolve(serverKey + ".json");
            
            List<Waypoint> waypoints = waypointsByServer.getOrDefault(serverKey, new ArrayList<>());
            
            try (Writer writer = Files.newBufferedWriter(file)) {
                GSON.toJson(waypoints, writer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void addWaypoint(Waypoint waypoint) {
        String serverKey = getServerKey();
        List<Waypoint> waypoints = waypointsByServer.computeIfAbsent(serverKey, k -> new ArrayList<>());
        waypoints.add(waypoint);
        save(serverKey);
    }
    
    public static void removeWaypoint(Waypoint waypoint) {
        String serverKey = getServerKey();
        List<Waypoint> waypoints = waypointsByServer.get(serverKey);
        if (waypoints != null) {
            waypoints.remove(waypoint);
            save(serverKey);
        }
    }
    
    public static List<Waypoint> getWaypoints() {
        String serverKey = getServerKey();
        return waypointsByServer.getOrDefault(serverKey, new ArrayList<>());
    }
    
    public static List<Waypoint> getWaypointsForDimension(String dimension) {
        return getWaypoints().stream()
            .filter(wp -> wp.dimension.equals(dimension))
            .toList();
    }
    
    public static void clearAll() {
        String serverKey = getServerKey();
        waypointsByServer.remove(serverKey);
        save(serverKey);
    }
    
    public static void exportToFile(Path exportPath) throws IOException {
        String serverKey = getServerKey();
        List<Waypoint> waypoints = getWaypoints();
        
        try (Writer writer = Files.newBufferedWriter(exportPath)) {
            GSON.toJson(waypoints, writer);
        }
    }
    
    public static void importFromFile(Path importPath) throws IOException {
        try (Reader reader = Files.newBufferedReader(importPath)) {
            Type listType = new TypeToken<List<Waypoint>>(){}.getType();
            List<Waypoint> waypoints = GSON.fromJson(reader, listType);
            
            if (waypoints != null) {
                String serverKey = getServerKey();
                waypointsByServer.put(serverKey, waypoints);
                save(serverKey);
            }
        }
    }
}
