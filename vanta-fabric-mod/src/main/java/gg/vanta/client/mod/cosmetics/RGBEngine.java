package gg.vanta.client.mod.cosmetics;

import java.awt.Color;

public class RGBEngine {
    public enum RgbMode {
        RAINBOW, PULSE, WAVE, STATIC, GRADIENT
    }
    
    private static RgbMode mode = RgbMode.RAINBOW;
    private static double speed = 1.0;
    private static double saturation = 1.0;
    private static double brightness = 1.0;
    
    private static long startTime = System.currentTimeMillis();
    
    /**
     * Get current RGB color as packed integer (ARGB format)
     */
    public static int getRGB(float offset) {
        long time = System.currentTimeMillis() - startTime;
        double t = (time * 0.001 * speed) + offset;
        
        return switch (mode) {
            case RAINBOW -> rainbowColor(t);
            case PULSE -> pulseColor(t);
            case WAVE -> waveColor(t, offset);
            case STATIC -> staticColor();
            case GRADIENT -> gradientColor(t);
        };
    }
    
    /**
     * Get RGB color with alpha
     */
    public static int getRGB(int alpha) {
        int rgb = getRGB(0.0f);
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }
    
    private static int rainbowColor(double t) {
        float hue = (float)(t % 1.0);
        Color color = Color.getHSBColor(hue, (float)saturation, (float)brightness);
        return 0xFF000000 | color.getRGB();
    }
    
    private static int pulseColor(double t) {
        float value = (float)((Math.sin(t * Math.PI * 2) + 1) / 2);
        float adjustedValue = value * (float)brightness + (1 - (float)brightness) * 0.5f;
        Color color = Color.getHSBColor((float)(t * 0.1), (float)saturation, adjustedValue);
        return 0xFF000000 | color.getRGB();
    }
    
    private static int waveColor(double t, float offset) {
        float hue = (float)((t + offset * 0.1) % 1.0);
        Color color = Color.getHSBColor(hue, (float)saturation, (float)brightness);
        return 0xFF000000 | color.getRGB();
    }
    
    private static int staticColor() {
        // VANTA green
        return 0xFF00FF87;
    }
    
    private static int gradientColor(double t) {
        // Smooth gradient through preset colors
        double cycle = t % 5.0;
        if (cycle < 1.0) {
            return lerpColor(0xFF00FF87, 0xFF00FFFF, cycle); // Green to Cyan
        } else if (cycle < 2.0) {
            return lerpColor(0xFF00FFFF, 0xFF0080FF, cycle - 1.0); // Cyan to Blue
        } else if (cycle < 3.0) {
            return lerpColor(0xFF0080FF, 0xFFFF00FF, cycle - 2.0); // Blue to Magenta
        } else if (cycle < 4.0) {
            return lerpColor(0xFFFF00FF, 0xFFFF8000, cycle - 3.0); // Magenta to Orange
        } else {
            return lerpColor(0xFFFF8000, 0xFF00FF87, cycle - 4.0); // Orange to Green
        }
    }
    
    private static int lerpColor(int from, int to, double t) {
        int r = (int)Math.round(((from >> 16) & 0xFF) * (1 - t) + ((to >> 16) & 0xFF) * t);
        int g = (int)Math.round(((from >> 8) & 0xFF) * (1 - t) + ((to >> 8) & 0xFF) * t);
        int b = (int)Math.round((from & 0xFF) * (1 - t) + (to & 0xFF) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
    
    /**
     * Apply RGB color to HUD element
     * Returns the color with original alpha preserved
     */
    public static int applyToColor(int baseColor, float offset) {
        int rgb = getRGB(offset);
        int alpha = (baseColor >> 24) & 0xFF;
        return (alpha << 24) | (rgb & 0xFFFFFF);
    }
    
    public static void setMode(RgbMode mode) {
        RGBEngine.mode = mode;
    }
    
    public static RgbMode getMode() {
        return mode;
    }
    
    public static void setSpeed(double speed) {
        RGBEngine.speed = Math.clamp(speed, 0.1, 10.0);
    }
    
    public static double getSpeed() {
        return speed;
    }
    
    public static void setSaturation(double saturation) {
        RGBEngine.saturation = Math.clamp(saturation, 0.0, 1.0);
    }
    
    public static double getSaturation() {
        return saturation;
    }
    
    public static void setBrightness(double brightness) {
        RGBEngine.brightness = Math.clamp(brightness, 0.0, 1.0);
    }
    
    public static double getBrightness() {
        return brightness;
    }
}
