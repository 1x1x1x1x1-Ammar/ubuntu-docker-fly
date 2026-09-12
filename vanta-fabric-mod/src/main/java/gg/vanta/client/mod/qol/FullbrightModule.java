package gg.vanta.client.mod.qol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.world.LightType;

public class FullbrightModule {
    private static boolean enabled = false;
    private static float previousGamma = 0.5f;
    private static float transitionProgress = 0.0f;
    private static final float TRANSITION_DURATION = 0.3f;
    
    public static void tick() {
        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc.options == null) return;
        
        if (enabled) {
            // Smooth transition to fullbright
            if (transitionProgress < 1.0f) {
                transitionProgress = Math.min(1.0f, transitionProgress + (1.0f / TRANSITION_DURATION) * 0.016f);
            }
            
            float targetGamma = 16.0f;
            mc.options.getGamma().setValue((float) (previousGamma + (targetGamma - previousGamma) * transitionProgress));
        } else {
            // Smooth transition back
            if (transitionProgress > 0.0f) {
                transitionProgress = Math.max(0.0f, transitionProgress - (1.0f / TRANSITION_DURATION) * 0.016f);
            }
            
            float targetGamma = previousGamma;
            mc.options.getGamma().setValue((float) (targetGamma * transitionProgress + 0.5f * (1.0f - transitionProgress)));
        }
    }
    
    public static void toggle() {
        enabled = !enabled;
        if (enabled) {
            previousGamma = (float) MinecraftClient.getInstance().options.getGamma().getValue();
        }
    }
    
    public static void setEnabled(boolean enabled) {
        FullbrightModule.enabled = enabled;
        if (enabled) {
            previousGamma = (float) MinecraftClient.getInstance().options.getGamma().getValue();
        }
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
}
