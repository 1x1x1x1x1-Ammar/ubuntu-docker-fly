package gg.vanta.client.mod.gui.modules;

import gg.vanta.client.mod.config.VantaConfig;
import net.minecraft.client.gui.DrawContext;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager {
    private static ModuleManager INSTANCE;
    private final List<Module> modules = new ArrayList<>();
    private Module selectedModule = null;
    
    public static ModuleManager get() {
        if (INSTANCE == null) {
            INSTANCE = new ModuleManager();
            INSTANCE.init();
        }
        return INSTANCE;
    }
    
    private void init() {
        // HUD Modules
        modules.add(new Module("FPS Counter", "Display current FPS", Category.HUD, 296));
        modules.add(new Module("Ping Display", "Show network ping", Category.HUD, 297));
        modules.add(new Module("Coordinates", "Show XYZ coordinates", Category.HUD, 303));
        modules.add(new Module("Direction", "Show facing direction", Category.HUD, 304));
        modules.add(new Module("Biome Info", "Display current biome", Category.HUD, 305));
        modules.add(new Module("Time Display", "Show game and real time", Category.HUD, 306));
        
        // Player Modules
        modules.add(new Module("Armor Status", "Show armor durability", Category.PLAYER, 310));
        modules.add(new Module("Potion Effects", "Display active potions", Category.PLAYER, 311));
        modules.add(new Module("Saturation Bar", "Show saturation level", Category.PLAYER, 312));
        modules.add(new Module("XP Bar", "Enhanced XP display", Category.PLAYER, 313));
        modules.add(new Module("Health Display", "Numeric health display", Category.PLAYER, 314));
        modules.add(new Module("Air Bubbles", "Enhanced air display", Category.PLAYER, 315));
        
        // PvP Modules
        modules.add(new Module("Keystrokes", "Show WASD + clicks", Category.PVP, 320));
        modules.add(new Module("CPS Counter", "Clicks per second", Category.PVP, 321));
        modules.add(new Module("Reach Display", "Show attack reach", Category.PVP, 322));
        modules.add(new Module("Cooldown Arc", "Attack cooldown indicator", Category.PVP, 323));
        modules.add(new Module("Target Health", "Show target HP", Category.PVP, 324));
        modules.add(new Module("Sprint Status", "Sprint indicator", Category.PVP, 325));
        
        // World Modules
        modules.add(new Module("Mini Map", "Xaero-style minimap", Category.WORLD, 330));
        modules.add(new Module("Waypoints", "Custom waypoints", Category.WORLD, 331));
        modules.add(new Module("Chunk Coords", "Show chunk coordinates", Category.WORLD, 332));
        modules.add(new Module("Light Level", "Mob spawn light check", Category.WORLD, 333));
        modules.add(new Module("Slime Chunks", "Slime chunk indicator", Category.WORLD, 334));
        modules.add(new Module("Weather Info", "Weather status", Category.WORLD, 335));
        modules.add(new Module("Moon Phase", "Current moon phase", Category.WORLD, 336));
        
        // Social Modules
        modules.add(new Module("Scoreboard", "Custom scoreboard", Category.SOCIAL, 340));
        modules.add(new Module("Tab List", "Styled player list", Category.SOCIAL, 341));
        modules.add(new Module("Chat Timestamps", "Add timestamps to chat", Category.SOCIAL, 342));
        modules.add(new Module("Death Messages", "Highlight your deaths", Category.SOCIAL, 343));
        
        // Cosmetics Modules
        modules.add(new Module("Capes", "Custom cape renderer", Category.COSMETICS, 350));
        modules.add(new Module("Wings", "Decorative wings", Category.COSMETICS, 351));
        modules.add(new Module("Trail Effects", "Footstep particles", Category.COSMETICS, 352));
        modules.add(new Module("RGB HUD", "Rainbow HUD elements", Category.COSMETICS, 353));
        
        // Performance Modules
        modules.add(new Module("FPS Limiter", "Limit background FPS", Category.PERFORMANCE, 360));
        modules.add(new Module("Particle Reducer", "Reduce particle count", Category.PERFORMANCE, 361));
        modules.add(new Module("Entity Culling", "Hide hidden entities", Category.PERFORMANCE, 362));
        modules.add(new Module("Smart Chunks", "Optimized chunk loading", Category.PERFORMANCE, 363));
        
        // Load saved configs
        loadConfigs();
    }
    
    private void loadConfigs() {
        VantaConfig config = VantaConfig.get();
        for (Module module : modules) {
            VantaConfig.ConfigData.ModuleConfig moduleConfig = config.getModuleConfig(module.id);
            module.setEnabled(moduleConfig.enabled);
            module.setKeybind(moduleConfig.keybind);
        }
    }
    
    public void saveConfigs() {
        VantaConfig config = VantaConfig.get();
        for (Module module : modules) {
            VantaConfig.ConfigData.ModuleConfig moduleConfig = config.getModuleConfig(module.id);
            moduleConfig.enabled = module.isEnabled();
            moduleConfig.keybind = module.getKeybind();
        }
        config.save();
    }
    
    public List<Module> getModulesForCategory(Module.Category category) {
        List<Module> result = new ArrayList<>();
        for (Module module : modules) {
            if (module.getCategory() == category) {
                result.add(module);
            }
        }
        return result;
    }
    
    public Module getModuleByName(String name) {
        for (Module module : modules) {
            if (module.getName().equals(name)) {
                return module;
            }
        }
        return null;
    }
    
    public Module getSelectedModule() {
        return selectedModule;
    }
    
    public void setSelectedModule(Module module) {
        this.selectedModule = module;
    }
    
    public List<Module> getAllModules() {
        return modules;
    }
    
    public void toggleModule(Module module) {
        module.setEnabled(!module.isEnabled());
        saveConfigs();
    }
}
