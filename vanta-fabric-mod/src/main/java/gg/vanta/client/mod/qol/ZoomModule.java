package gg.vanta.client.mod.qol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class ZoomModule {
    private static float currentZoom = 1.0f;
    private static float targetZoom = 1.0f;
    private static boolean isZooming = false;
    private static double zoomLevel = 1.0;
    
    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.player == null) return;
        
        // Check zoom key (C by default)
        int zoomKey = GLFW.GLFW_KEY_C;
        boolean zoomPressed = InputUtil.isKeyPressed(mc.getWindow().getHandle(), zoomKey);
        
        if (zoomPressed) {
            isZooming = true;
            targetZoom = (float) zoomLevel;
        } else {
            isZooming = false;
            targetZoom = 1.0f;
        }
        
        // Smooth zoom transition
        currentZoom += (targetZoom - currentZoom) * 0.1f;
        
        // Apply zoom
        if (currentZoom != 1.0f) {
            mc.options.getFovEffectScale().setValue(0.0);
        }
    }
    
    public static float getFOVModifier(float fov) {
        return fov / currentZoom;
    }
    
    public static void setZoomLevel(double level) {
        zoomLevel = Math.clamp(level, 1.0, 30.0);
    }
    
    public static double getZoomLevel() {
        return zoomLevel;
    }
    
    public static boolean isZooming() {
        return isZooming && currentZoom > 1.01f;
    }
}
