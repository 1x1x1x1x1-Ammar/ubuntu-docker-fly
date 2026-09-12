package gg.vanta.client.mod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class VantaConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static VantaConfig INSTANCE;
    
    public ConfigData data = new ConfigData();
    private Path configPath;
    
    public static VantaConfig get() {
        if (INSTANCE == null) {
            INSTANCE = new VantaConfig();
            INSTANCE.load();
        }
        return INSTANCE;
    }
    
    public static class ConfigData {
        // HUD Settings
        public boolean hudEnabled = true;
        public Map<String, HudElementConfig> hudElements = new HashMap<>();
        
        // Click GUI
        public int clickGuiKey = 54; // RIGHT_SHIFT
        public String activeProfile = "default";
        public Map<String, ModuleConfig> modules = new HashMap<>();
        
        // QoL
        public int zoomKey = 29; // C
        public double maxZoom = 10.0;
        public double zoomSmoothness = 0.5;
        public boolean fullbrightEnabled = false;
        public int fullbrightKey = 76; // L
        public boolean autoReconnect = true;
        public int autoReconnectDelay = 5;
        
        // Waypoints
        public Map<String, WaypointConfig> waypoints = new HashMap<>();
        public boolean waypointBeams = true;
        public double beamHeight = 256;
        
        // Cosmetics
        public boolean cosmeticsEnabled = true;
        public String equippedCape = "vanta_default";
        public boolean wingsEnabled = false;
        public String trailType = "none";
        public boolean rgbHudEnabled = false;
        public int rgbSpeed = 5;
        
        // Performance
        public int fpsLimitBackground = 30;
        public int particleReduction = 0;
        public int entityRenderDistance = 100;
        public boolean smartChunkLoading = true;
        
        // RGB Settings
        public RgbConfig rgbConfig = new RgbConfig();
        
        public static class RgbConfig {
            public String mode = "rainbow";
            public double speed = 1.0;
            public double saturation = 1.0;
            public double brightness = 1.0;
        }
        
        public static class HudElementConfig {
            public boolean enabled = true;
            public double x = 0;
            public double y = 0;
            public double scale = 1.0;
            public int color = 0xFFFFFFFF;
            public boolean shadow = true;
        }
        
        public static class ModuleConfig {
            public boolean enabled = false;
            public int keybind = -1;
            public Map<String, Object> settings = new HashMap<>();
        }
        
        public static class WaypointConfig {
            public String name;
            public double x, y, z;
            public String dimension;
            public int color = 0xFF00FF87;
            public boolean visible = true;
        }
    }
    
    public void load() {
        configPath = FabricLoader.getInstance().getConfigDir().resolve("vanta.json");
        if (Files.exists(configPath)) {
            try (Reader reader = Files.newBufferedReader(configPath)) {
                data = GSON.fromJson(reader, ConfigData.class);
                if (data == null) data = new ConfigData();
            } catch (Exception e) {
                e.printStackTrace();
                data = new ConfigData();
            }
        }
        save();
    }
    
    public void save() {
        try {
            Files.createDirectories(configPath.getParent());
            try (Writer writer = Files.newBufferedWriter(configPath)) {
                GSON.toJson(data, writer);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public ConfigData.HudElementConfig getHudElementConfig(String id) {
        return data.hudElements.computeIfAbsent(id, k -> new ConfigData.HudElementConfig());
    }
    
    public ConfigData.ModuleConfig getModuleConfig(String id) {
        return data.modules.computeIfAbsent(id, k -> new ConfigData.ModuleConfig());
    }
}
