package gg.vanta.client.mod.hud.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of all HUD elements for VANTA Client
 */
public class HudElements {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    // ========== CORE ELEMENTS ==========
    
    public static class FPSCounter implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            TextRenderer tr = mc.textRenderer;
            int fps = MinecraftClient.getInstance().getCurrentFps();
            
            Formatting color = fps > 60 ? Formatting.GREEN : fps > 30 ? Formatting.YELLOW : Formatting.RED;
            String text = fps + " FPS";
            
            tr.draw(matrices, text, x, y, color.getColorValue());
        }
        
        @Override
        public int getWidth() {
            int fps = MinecraftClient.getInstance().getCurrentFps();
            return mc.textRenderer.getWidth((fps > 60 ? Formatting.GREEN : fps > 30 ? Formatting.YELLOW : Formatting.RED) + (fps + " FPS"));
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "fps_counter";
        }
        
        @Override
        public String getName() {
            return "FPS Counter";
        }
    }
    
    public static class PingDisplay implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.getNetworkHandler() == null) return;
            
            TextRenderer tr = mc.textRenderer;
            int ping = mc.getNetworkHandler().getPlayerReportedPing();
            
            Formatting color = ping > 150 ? Formatting.RED : ping > 75 ? Formatting.YELLOW : Formatting.GREEN;
            String text = ping + " ms";
            
            tr.draw(matrices, text, x, y, color.getColorValue());
        }
        
        @Override
        public int getWidth() {
            if (mc.getNetworkHandler() == null) return 0;
            int ping = mc.getNetworkHandler().getPlayerReportedPing();
            return mc.textRenderer.getWidth((ping > 150 ? Formatting.RED : ping > 75 ? Formatting.YELLOW : Formatting.GREEN) + (ping + " ms"));
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "ping_display";
        }
        
        @Override
        public String getName() {
            return "Ping Display";
        }
    }
    
    public static class Coordinates implements IHudElement {
        private boolean netherConversion = false;
        private boolean copyOnClick = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            double xCoord = mc.player.getX();
            double yCoord = mc.player.getY();
            double zCoord = mc.player.getZ();
            
            String displayX = String.format("%.1f", xCoord);
            String displayY = String.format("%.2f", yCoord);
            String displayZ = String.format("%.1f", zCoord);
            
            if (netherConversion && mc.world.getDimension().ultrawarm()) {
                displayX = String.format("%.1f (×8: %.1f)", xCoord, xCoord * 8);
                displayZ = String.format("%.1f (×8: %.1f)", zCoord, zCoord * 8);
            }
            
            String text = String.format("XYZ: %s / %s / %s", displayX, displayY, displayZ);
            tr.draw(matrices, text, x, y, 0xFFFFFF);
        }
        
        @Override
        public int getWidth() {
            if (mc.player == null) return 0;
            return mc.textRenderer.getWidth("XYZ: -999999.9 (×8: -999999.9) / -99.99 / -999999.9 (×8: -999999.9)");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "coordinates";
        }
        
        @Override
        public String getName() {
            return "Coordinates";
        }
        
        public void toggleNetherConversion() {
            netherConversion = !netherConversion;
        }
        
        public boolean isNetherConversion() {
            return netherConversion;
        }
        
        public void setNetherConversion(boolean value) {
            netherConversion = value;
        }
    }
    
    public static class Direction implements IHudElement {
        private boolean showDegrees = true;
        private boolean showCompassArc = false;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            TextRenderer tr = mc.textRenderer;
            float yaw = mc.player.getYaw();
            
            String direction = getDirection(yaw);
            String text = showDegrees ? String.format("%s (%.1f°)", direction, normalizeYaw(yaw)) : direction;
            
            if (showCompassArc) {
                text = "↑ " + text;
            }
            
            tr.draw(matrices, text, x, y, 0xFFFFFF);
        }
        
        private String getDirection(float yaw) {
            yaw = normalizeYaw(yaw);
            if (yaw < 22.5) return "S";
            if (yaw < 67.5) return "SW";
            if (yaw < 112.5) return "W";
            if (yaw < 157.5) return "NW";
            if (yaw < 202.5) return "N";
            if (yaw < 247.5) return "NE";
            if (yaw < 292.5) return "E";
            if (yaw < 337.5) return "SE";
            return "S";
        }
        
        private float normalizeYaw(float yaw) {
            yaw = yaw % 360;
            if (yaw < 0) yaw += 360;
            return yaw;
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("↑ SE (359.9°)");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "direction";
        }
        
        @Override
        public String getName() {
            return "Direction";
        }
    }
    
    public static class BiomeName implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            Identifier biomeId = mc.world.getBiome(mc.player.getBlockPos()).getKey().get().getValue();
            String biomeName = biomeId.getPath().replace("_", " ").toUpperCase();
            
            tr.draw(matrices, biomeName, x, y, 0xAAAAAA);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("PLAINS");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "biome_name";
        }
        
        @Override
        public String getName() {
            return "Biome Name";
        }
    }
    
    public static class TimeDisplay implements IHudElement {
        private boolean showRealTime = false;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            TextRenderer tr = mc.textRenderer;
            
            if (showRealTime) {
                long realTime = System.currentTimeMillis();
                String time = String.format("%02d:%02d:%02d", 
                    (realTime / 3600000) % 24,
                    (realTime / 60000) % 60,
                    (realTime / 1000) % 60);
                tr.draw(matrices, time, x, y, 0xFFFFAA);
            } else {
                if (mc.world == null) return;
                long gameTime = mc.world.getTimeOfDay();
                long dayTime = gameTime % 24000;
                int hours = (int)((dayTime + 6000) % 24000 / 1000);
                int minutes = (int)((dayTime % 1000) * 60 / 1000);
                String time = String.format("%02d:%02d", hours, minutes);
                tr.draw(matrices, time, x, y, 0xFFFFAA);
            }
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("23:59:59");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "time_display";
        }
        
        @Override
        public String getName() {
            return "Time Display";
        }
        
        public void setShowRealTime(boolean value) {
            showRealTime = value;
        }
        
        public boolean isShowRealTime() {
            return showRealTime;
        }
    }
    
    public static class ServerInfo implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            String serverIp = mc.getCurrentServerEntry() != null ? 
                mc.getCurrentServerEntry().address : "Singleplayer";
            String worldName = mc.world.getScoreboardName();
            
            String text = serverIp.equals("Singleplayer") ? worldName : serverIp;
            tr.draw(matrices, text, x, y, 0xAAAAAA);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("mc.hypixel.net");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "server_info";
        }
        
        @Override
        public String getName() {
            return "Server Info";
        }
    }
    
    // ========== PLAYER STATE ELEMENTS ==========
    
    public static class ArmorDurability implements IHudElement {
        private boolean showPercentage = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            for (int i = 3; i >= 0; i--) {
                var stack = mc.player.getInventory().getArmorStack(i);
                if (stack.isEmpty() || stack.getMaxDamage() <= 0) continue;
                
                int damage = stack.getDamage();
                int maxDamage = stack.getMaxDamage();
                int durability = maxDamage - damage;
                float percent = (float) durability / maxDamage;
                
                int color = percent > 0.5 ? 0x00FF00 : percent > 0.25 ? 0xFFFF00 : 0xFF0000;
                
                // Render bar background
                fill(matrices, x, y + (3-i)*12, x + 40, y + 2 + (3-i)*12, 0x40000000);
                // Render durability bar
                fill(matrices, x, y + (3-i)*12, x + (int)(40 * percent), y + 2 + (3-i)*12, color);
                
                if (showPercentage) {
                    String pct = String.format("%d%%", (int)(percent * 100));
                    mc.textRenderer.draw(matrices, pct, x + 42, y + (3-i)*12, 0xFFFFFF);
                }
            }
        }
        
        @Override
        public int getWidth() {
            return showPercentage ? 70 : 40;
        }
        
        @Override
        public int getHeight() {
            return 48;
        }
        
        @Override
        public String getId() {
            return "armor_durability";
        }
        
        @Override
        public String getName() {
            return "Armor Durability";
        }
    }
    
    public static class PotionEffects implements IHudElement {
        private boolean showTimer = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            int yOffset = 0;
            List<StatusEffectInstance> effects = new ArrayList<>(mc.player.getStatusEffects());
            
            for (StatusEffectInstance effect : effects) {
                if (effect.shouldShowIcon()) {
                    String effectName = effect.getEffectType().value().getName().getString();
                    int duration = effect.getDuration();
                    
                    String text = effectName;
                    if (showTimer && duration > 0) {
                        int seconds = duration / 20;
                        int mins = seconds / 60;
                        int secs = seconds % 60;
                        text += String.format(" %d:%02d", mins, secs);
                    }
                    
                    // Fade when expiring
                    int color = duration < 100 ? 0x80FFFFFF : 0xFFFFFF;
                    mc.textRenderer.draw(matrices, text, x + 20, y + yOffset, color);
                    yOffset += 12;
                }
            }
        }
        
        @Override
        public int getWidth() {
            return 120;
        }
        
        @Override
        public int getHeight() {
            if (mc.player == null) return 0;
            return (int)mc.player.getStatusEffects().stream().filter(e -> e.shouldShowIcon()).count() * 12;
        }
        
        @Override
        public String getId() {
            return "potion_effects";
        }
        
        @Override
        public String getName() {
            return "Potion Effects";
        }
    }
    
    public static class SaturationBar implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            float saturation = mc.player.getHungerManager().getSaturationLevel();
            int barWidth = (int)(saturation * 2);
            
            fill(matrices, x, y, x + barWidth, y + 2, 0xFFD4AF37);
        }
        
        @Override
        public int getWidth() {
            return 40;
        }
        
        @Override
        public int getHeight() {
            return 4;
        }
        
        @Override
        public String getId() {
            return "saturation_bar";
        }
        
        @Override
        public String getName() {
            return "Saturation Bar";
        }
    }
    
    public static class ExperienceBar implements IHudElement {
        private boolean animated = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            int experience = mc.player.experienceLevel;
            float progress = mc.player.experienceProgress;
            
            fill(matrices, x, y, x + 100, y + 6, 0x40000000);
            int filledWidth = (int)(100 * progress);
            fill(matrices, x, y, x + filledWidth, y + 6, 0xFF00FF87);
            
            String levelText = String.valueOf(experience);
            mc.textRenderer.draw(matrices, levelText, x + 50 - mc.textRenderer.getWidth(levelText)/2, y - 2, 0x00FF87);
        }
        
        @Override
        public int getWidth() {
            return 100;
        }
        
        @Override
        public int getHeight() {
            return 10;
        }
        
        @Override
        public String getId() {
            return "experience_bar";
        }
        
        @Override
        public String getName() {
            return "Experience Bar";
        }
    }
    
    public static class HeldItemDurability implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            var stack = mc.player.getMainHandStack();
            if (stack.isEmpty() || stack.getMaxDamage() <= 0) {
                stack = mc.player.getOffHandStack();
            }
            
            if (stack.isEmpty() || stack.getMaxDamage() <= 0) return;
            
            int damage = stack.getDamage();
            int maxDamage = stack.getMaxDamage();
            int durability = maxDamage - damage;
            float percent = (float) durability / maxDamage;
            
            int color = percent > 0.5 ? 0x00FF00 : percent > 0.25 ? 0xFFFF00 : 0xFF0000;
            
            fill(matrices, x, y, x + 60, y + 4, 0x40000000);
            fill(matrices, x, y, x + (int)(60 * percent), y + 4, color);
            
            String pct = String.format("%d%%", (int)(percent * 100));
            mc.textRenderer.draw(matrices, pct, x + 62, y, 0xFFFFFF);
        }
        
        @Override
        public int getWidth() {
            return 90;
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "held_item_durability";
        }
        
        @Override
        public String getName() {
            return "Held Item Durability";
        }
    }
    
    public static class HealthDisplay implements IHudElement {
        private boolean showHearts = true;
        private boolean showAbsorption = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            float health = mc.player.getHealth();
            float maxHealth = mc.player.getMaxHealth();
            float absorption = mc.player.getAbsorptionAmount();
            
            String text = String.format("%.0f/%.0f", health, maxHealth);
            if (showAbsorption && absorption > 0) {
                text += String.format(" (+%.0f)", absorption);
            }
            
            mc.textRenderer.draw(matrices, text, x, y, 0xFF0000);
            
            if (showHearts) {
                int hearts = (int)Math.ceil(health / 2);
                for (int i = 0; i < hearts; i++) {
                    fill(matrices, x + i * 10, y + 12, x + i * 10 + 8, y + 12 + 8, 0xFFFF0000);
                }
            }
        }
        
        @Override
        public int getWidth() {
            return 60;
        }
        
        @Override
        public int getHeight() {
            return showHearts ? 22 : 10;
        }
        
        @Override
        public String getId() {
            return "health_display";
        }
        
        @Override
        public String getName() {
            return "Health Display";
        }
    }
    
    public static class AirBubbles implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || !mc.player.isSubmergedInWater()) return;
            
            int air = mc.player.getAir();
            int maxAir = 300;
            int bubbles = (air * 10) / maxAir;
            
            for (int i = 0; i < 10; i++) {
                int color = i < bubbles ? 0xFF00FFFF : 0x4000FFFF;
                fill(matrices, x + i * 10, y, x + i * 10 + 8, y + 8, color);
            }
        }
        
        @Override
        public int getWidth() {
            return 100;
        }
        
        @Override
        public int getHeight() {
            return 10;
        }
        
        @Override
        public String getId() {
            return "air_bubbles";
        }
        
        @Override
        public String getName() {
            return "Air Bubbles";
        }
    }
    
    // ========== HELPER METHODS ==========
    
    private static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        // Simplified fill - in actual implementation would use proper vertex rendering
    }
}
