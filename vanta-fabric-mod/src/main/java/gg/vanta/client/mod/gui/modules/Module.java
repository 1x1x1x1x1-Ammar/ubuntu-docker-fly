package gg.vanta.client.mod.gui.modules;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final String name;
    public final String description;
    public final Category category;
    public final int id;
    
    private boolean enabled = false;
    private int keybind = -1;
    private final List<Setting<?>> settings = new ArrayList<>();
    
    public Module(String name, String description, Category category, int id) {
        this.name = name;
        this.description = description;
        this.category = category;
        this.id = id;
        
        // Initialize default settings based on module type
        initSettings();
    }
    
    private void initSettings() {
        // Common settings for all modules
        settings.add(new ColorSetting("color", "Element color", 0xFF00FF87));
        settings.add(new BoolSetting("shadow", "Text shadow", true));
        
        // Module-specific settings
        switch (id) {
            case 296: // FPS Counter
                settings.add(new BoolSetting("showGraph", "Show FPS graph", false));
                settings.add(new IntSetting("refreshRate", "Refresh rate (ms)", 500, 100, 2000));
                break;
            case 303: // Coordinates
                settings.add(new BoolSetting("showNether", "Show nether coords", true));
                settings.add(new BoolSetting("copyOnClick", "Copy on click", true));
                break;
            case 320: // Keystrokes
                settings.add(new BoolSetting("showCPSONKeys", "Show CPS on keys", true));
                settings.add(new DoubleSetting("fadeSpeed", "Fade speed", 0.1, 0.01, 1.0));
                break;
            case 321: // CPS Counter
                settings.add(new IntSetting("averageTime", "Average time (s)", 1, 1, 5));
                break;
            case 330: // Mini Map
                settings.add(new DoubleSetting("zoom", "Zoom level", 1.0, 0.5, 4.0));
                settings.add(new BoolSetting("northUp", "North always up", false));
                settings.add(new BoolSetting("showMobs", "Show mob dots", true));
                break;
            case 350: // Capes
                settings.add(new EnumSetting("capeType", "Cape type", new String[]{"vanta_default", "phantom", "emerald", "void", "custom"}));
                settings.add(new ColorSetting("tint", "Color tint", 0xFFFFFFFF));
                break;
            case 352: // Trail Effects
                settings.add(new EnumSetting("trailType", "Trail type", new String[]{"green_sparkle", "shadow_wisps", "ember", "void", "rainbow"}));
                settings.add(new DoubleSetting("density", "Particle density", 1.0, 0.1, 2.0));
                break;
            case 353: // RGB HUD
                settings.add(new EnumSetting("mode", "RGB mode", new String[]{"rainbow", "pulse", "wave", "static", "gradient"}));
                settings.add(new DoubleSetting("speed", "Speed", 1.0, 0.1, 10.0));
                settings.add(new DoubleSetting("saturation", "Saturation", 1.0, 0.0, 1.0));
                settings.add(new DoubleSetting("brightness", "Brightness", 1.0, 0.0, 1.0));
                break;
        }
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public int getKeybind() {
        return keybind;
    }
    
    public void setKeybind(int keybind) {
        this.keybind = keybind;
    }
    
    public String getKeybindName() {
        if (keybind == -1) return "None";
        return GLFW.glfwGetKeyName(keybind, -1);
    }
    
    public Category getCategory() {
        return category;
    }
    
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public List<Setting<?>> getSettings() {
        return settings;
    }
    
    @SuppressWarnings("unchecked")
    public <T> T getSettingValue(String name) {
        for (Setting<?> setting : settings) {
            if (setting.name.equals(name)) {
                return (T) setting.value;
            }
        }
        return null;
    }
    
    public enum Category {
        HUD, RENDER, WORLD, PLAYER, PVP, SOCIAL, COSMETICS, PERFORMANCE
    }
    
    // Setting classes
    public static abstract class Setting<T> {
        public final String name;
        public final String description;
        public T value;
        public final T defaultValue;
        
        public Setting(String name, String description, T defaultValue) {
            this.name = name;
            this.description = description;
            this.defaultValue = defaultValue;
            this.value = defaultValue;
        }
        
        public abstract int render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta);
        public abstract void reset();
    }
    
    public static class BoolSetting extends Setting<Boolean> {
        public BoolSetting(String name, String description, boolean defaultValue) {
            super(name, description, defaultValue);
        }
        
        @Override
        public int render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
            context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, name, x, y, 0xFFFFFFFF);
            
            // Render toggle
            int toggleX = x + 200;
            context.fill(toggleX, y, toggleX + 30, y + 14, value ? 0xFF00FF87 : 0xFF444444);
            int thumbX = value ? toggleX + 16 : toggleX;
            context.fill(thumbX, y + 2, thumbX + 12, y + 12, 0xFFFFFFFF);
            
            return 20;
        }
        
        @Override
        public void reset() {
            value = defaultValue;
        }
    }
    
    public static class IntSetting extends Setting<Integer> {
        public final int min, max;
        
        public IntSetting(String name, String description, int defaultValue, int min, int max) {
            super(name, description, defaultValue);
            this.min = min;
            this.max = max;
        }
        
        @Override
        public int render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
            var mc = MinecraftClient.getInstance();
            context.drawTextWithShadow(mc.textRenderer, name, x, y, 0xFFFFFFFF);
            context.drawTextWithShadow(mc.textRenderer, String.valueOf(value), x + 200, y, 0xFF00FF87);
            
            // Slider background
            context.fill(x + 250, y + 4, x + 350, y + 12, 0xFF333333);
            
            // Slider fill
            float percent = (float)(value - min) / (max - min);
            context.fill(x + 250, y + 4, (int)(x + 250 + (percent * 100)), y + 12, 0xFF00FF87);
            
            return 20;
        }
        
        @Override
        public void reset() {
            value = defaultValue;
        }
    }
    
    public static class DoubleSetting extends Setting<Double> {
        public final double min, max;
        
        public DoubleSetting(String name, String description, double defaultValue, double min, double max) {
            super(name, description, defaultValue);
            this.min = min;
            this.max = max;
        }
        
        @Override
        public int render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
            var mc = MinecraftClient.getInstance();
            context.drawTextWithShadow(mc.textRenderer, name, x, y, 0xFFFFFFFF);
            context.drawTextWithShadow(mc.textRenderer, String.format("%.2f", value), x + 200, y, 0xFF00FF87);
            
            // Slider
            context.fill(x + 250, y + 4, x + 350, y + 12, 0xFF333333);
            float percent = (float)((value - min) / (max - min));
            context.fill(x + 250, y + 4, (int)(x + 250 + (percent * 100)), y + 12, 0xFF00FF87);
            
            return 20;
        }
        
        @Override
        public void reset() {
            value = defaultValue;
        }
    }
    
    public static class ColorSetting extends Setting<Integer> {
        public ColorSetting(String name, String description, int defaultValue) {
            super(name, description, defaultValue);
        }
        
        @Override
        public int render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
            var mc = MinecraftClient.getInstance();
            context.drawTextWithShadow(mc.textRenderer, name, x, y, 0xFFFFFFFF);
            
            // Color preview box
            context.fill(x + 200, y, x + 230, y + 16, value);
            context.hline(x + 200, x + 230, y, 0xFFFFFFFF);
            context.hline(x + 200, x + 230, y + 16, 0xFFFFFFFF);
            context.vline(x + 200, y, y + 16, 0xFFFFFFFF);
            context.vline(x + 230, y, y + 16, 0xFFFFFFFF);
            
            // Hex value
            String hex = String.format("#%06X", value & 0xFFFFFF);
            context.drawTextWithShadow(mc.textRenderer, hex, x + 240, y, 0xFFAAAAAA);
            
            return 20;
        }
        
        @Override
        public void reset() {
            value = defaultValue;
        }
    }
    
    public static class EnumSetting extends Setting<String> {
        public final String[] options;
        private int currentIndex = 0;
        
        public EnumSetting(String name, String description, String[] options) {
            super(name, description, options[0]);
            this.options = options;
        }
        
        @Override
        public int render(DrawContext context, int x, int y, int mouseX, int mouseY, float delta) {
            var mc = MinecraftClient.getInstance();
            context.drawTextWithShadow(mc.textRenderer, name, x, y, 0xFFFFFFFF);
            
            // Dropdown box
            context.fill(x + 200, y, x + 350, y + 16, 0xFF333333);
            context.drawTextWithShadow(mc.textRenderer, value + " ▼", x + 210, y, 0xFF00FF87);
            
            return 20;
        }
        
        @Override
        public void reset() {
            value = defaultValue;
            currentIndex = 0;
        }
    }
}
