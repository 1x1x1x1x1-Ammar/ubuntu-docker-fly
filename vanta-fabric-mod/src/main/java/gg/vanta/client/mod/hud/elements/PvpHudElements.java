package gg.vanta.client.mod.hud.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;

/**
 * PVP and Gameplay related HUD elements
 */
public class PvpHudElements {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public static class KeystrokesDisplay implements IHudElement {
        private boolean showCPS = true;
        private int leftCPS = 0;
        private int rightCPS = 0;
        private long lastClickTime = 0;
        
        // Key states
        private boolean wPressed = false;
        private boolean aPressed = false;
        private boolean sPressed = false;
        private boolean dPressed = false;
        private boolean lmbPressed = false;
        private boolean rmbPressed = false;
        private boolean spacePressed = false;
        private boolean shiftPressed = false;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            TextRenderer tr = mc.textRenderer;
            
            // Render WASD keys
            renderKey(matrices, tr, x, y, "W", wPressed);
            renderKey(matrices, tr, x - 20, y + 12, "A", aPressed);
            renderKey(matrices, tr, x + 20, y + 12, "S", sPressed);
            renderKey(matrices, tr, x + 40, y + 12, "D", dPressed);
            
            // Render mouse buttons with CPS
            if (showCPS) {
                renderKeyWithCount(matrices, tr, x + 60, y, "LMB", lmbPressed, leftCPS);
                renderKeyWithCount(matrices, tr, x + 60, y + 12, "RMB", rmbPressed, rightCPS);
            } else {
                renderKey(matrices, tr, x + 60, y, "LMB", lmbPressed);
                renderKey(matrices, tr, x + 60, y + 12, "RMB", rmbPressed);
            }
            
            // Render SPACE and SHIFT
            renderKey(matrices, tr, x, y + 24, "SPACE", spacePressed);
            renderKey(matrices, tr, x + 80, y + 24, "SHIFT", shiftPressed);
        }
        
        private void renderKey(MatrixStack matrices, TextRenderer tr, int x, int y, String key, boolean pressed) {
            int bgColor = pressed ? 0xFF00FF87 : 0x40000000;
            int textColor = pressed ? 0xFF000000 : 0xFFFFFFFF;
            
            int width = tr.getWidth(key) + 8;
            fill(matrices, x, y, x + width, y + 10, bgColor);
            tr.draw(matrices, key, x + 4, y + 2, textColor);
        }
        
        private void renderKeyWithCount(MatrixStack matrices, TextRenderer tr, int x, int y, String key, boolean pressed, int count) {
            int bgColor = pressed ? 0xFF00FF87 : 0x40000000;
            int textColor = pressed ? 0xFF000000 : 0xFFFFFFFF;
            
            String display = showCPS ? key + " (" + count + ")" : key;
            int width = tr.getWidth(display) + 8;
            fill(matrices, x, y, x + width, y + 10, bgColor);
            tr.draw(matrices, display, x + 4, y + 2, textColor);
        }
        
        public void setKeyPressed(String key, boolean pressed) {
            switch (key.toUpperCase()) {
                case "W" -> wPressed = pressed;
                case "A" -> aPressed = pressed;
                case "S" -> sPressed = pressed;
                case "D" -> dPressed = pressed;
                case "LMB" -> lmbPressed = pressed;
                case "RMB" -> rmbPressed = pressed;
                case "SPACE" -> spacePressed = pressed;
                case "SHIFT" -> shiftPressed = pressed;
            }
        }
        
        public void recordClick(boolean left) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastClickTime < 1000) {
                if (left) leftCPS++;
                else rightCPS++;
            } else {
                leftCPS = left ? 1 : 0;
                rightCPS = left ? 0 : 1;
            }
            lastClickTime = currentTime;
        }
        
        public void updateCPS() {
            // Decay CPS over time
            if (System.currentTimeMillis() - lastClickTime > 1000) {
                leftCPS = 0;
                rightCPS = 0;
            }
        }
        
        @Override
        public int getWidth() {
            return 120;
        }
        
        @Override
        public int getHeight() {
            return 36;
        }
        
        @Override
        public String getId() {
            return "keystrokes";
        }
        
        @Override
        public String getName() {
            return "Keystrokes Display";
        }
    }
    
    public static class CPSCounter implements IHudElement {
        private int[] clickHistory = new int[100];
        private int currentIndex = 0;
        private long lastUpdate = 0;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            TextRenderer tr = mc.textRenderer;
            
            int cps1s = getCPS(1000);
            int cps5s = getCPS(5000);
            
            String text = String.format("CPS: %d (1s) | %d (5s)", cps1s, cps5s);
            tr.draw(matrices, text, x, y, 0x00FF87);
        }
        
        public void recordClick() {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastUpdate >= 10) {
                clickHistory[currentIndex] = 1;
                currentIndex = (currentIndex + 1) % clickHistory.length;
                lastUpdate = currentTime;
            }
        }
        
        private int getCPS(long milliseconds) {
            int samples = (int)(milliseconds / 10);
            int count = 0;
            for (int i = 0; i < Math.min(samples, clickHistory.length); i++) {
                int idx = (currentIndex - 1 - i + clickHistory.length) % clickHistory.length;
                count += clickHistory[idx];
            }
            return count;
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("CPS: 99 (1s) | 99 (5s)");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "cps_counter";
        }
        
        @Override
        public String getName() {
            return "CPS Counter";
        }
    }
    
    public static class ReachDisplay implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.crosshairTarget == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
                Vec3d eyes = mc.player.getEyePos();
                double distance = eyes.distanceTo(entityHit.getPos());
                
                String text = String.format("Reach: %.2f blocks", distance);
                tr.draw(matrices, text, x, y, 0x00FF87);
            }
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("Reach: 9.99 blocks");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "reach_display";
        }
        
        @Override
        public String getName() {
            return "Reach Display";
        }
    }
    
    public static class AttackCooldown implements IHudElement {
        private boolean showArc = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            float cooldown = mc.player.getAttackCooldownProgress(0);
            
            if (showArc) {
                // Render arc indicator
                renderCooldownArc(matrices, x, y, cooldown);
            } else {
                // Render bar
                fill(matrices, x, y, x + 50, y + 4, 0x40000000);
                fill(matrices, x, y, x + (int)(50 * cooldown), y + 4, 0x00FF87);
            }
        }
        
        private void renderCooldownArc(MatrixStack matrices, int centerX, int centerY, float progress) {
            // Simplified arc rendering - would use proper vertex rendering in actual implementation
            int radius = 10;
            
            // Draw full circle background
            fillCircle(matrices, centerX, centerY, radius, 0x40000000);
            
            // Draw progress arc
            if (progress >= 1.0f) {
                fillCircle(matrices, centerX, centerY, radius, 0x00FF87);
            }
        }
        
        @Override
        public int getWidth() {
            return 24;
        }
        
        @Override
        public int getHeight() {
            return 24;
        }
        
        @Override
        public String getId() {
            return "attack_cooldown";
        }
        
        @Override
        public String getName() {
            return "Attack Cooldown";
        }
    }
    
    public static class SprintStatus implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            if (mc.player.isSprinting()) {
                tr.draw(matrices, "→ SPRINTING", x, y, 0x00FF87);
            }
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("→ SPRINTING");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "sprint_status";
        }
        
        @Override
        public String getName() {
            return "Sprint Status";
        }
    }
    
    public static class JumpHeightTracker implements IHudElement {
        private double maxJumpHeight = 0;
        private boolean inAir = false;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            if (inAir && maxJumpHeight > 0) {
                String text = String.format("Jump: %.2f blocks", maxJumpHeight);
                tr.draw(matrices, text, x, y, 0xFFFF00);
            }
        }
        
        public void updateJumpHeight() {
            if (mc.player == null) return;
            
            boolean currentlyInAir = !mc.player.isOnGround();
            
            if (currentlyInAir && !inAir) {
                // Just jumped
                maxJumpHeight = 0;
            } else if (currentlyInAir && inAir) {
                // Still in air, track height
                double height = mc.player.getY() - Math.floor(mc.player.getY());
                if (height > maxJumpHeight) {
                    maxJumpHeight = height;
                }
            }
            
            inAir = currentlyInAir;
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("Jump: 9.99 blocks");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "jump_height";
        }
        
        @Override
        public String getName() {
            return "Jump Height Tracker";
        }
    }
    
    public static class TargetHealth implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.crosshairTarget == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            if (mc.crosshairTarget.getType() == HitResult.Type.ENTITY) {
                EntityHitResult entityHit = (EntityHitResult) mc.crosshairTarget;
                Entity target = entityHit.getEntity();
                
                if (target instanceof LivingEntity living) {
                    float health = living.getHealth();
                    float maxHealth = living.getMaxHealth();
                    
                    String name = target.getName().getString();
                    String text = String.format("%s: %.1f/%.1f", name, health, maxHealth);
                    
                    int color = health < maxHealth * 0.25 ? 0xFF0000 : 
                               health < maxHealth * 0.5 ? 0xFFFF00 : 0x00FF00;
                    
                    tr.draw(matrices, text, x, y, color);
                    
                    // Health bar
                    fill(matrices, x, y + 10, x + 100, y + 14, 0x40000000);
                    fill(matrices, x, y + 10, x + (int)(100 * (health / maxHealth)), y + 14, color);
                }
            }
        }
        
        @Override
        public int getWidth() {
            return 100;
        }
        
        @Override
        public int getHeight() {
            return 16;
        }
        
        @Override
        public String getId() {
            return "target_health";
        }
        
        @Override
        public String getName() {
            return "Target Health Bar";
        }
    }
    
    private static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        // Simplified fill implementation
    }
    
    private static void fillCircle(MatrixStack matrices, int centerX, int centerY, int radius, int color) {
        // Simplified circle fill
    }
}
