package gg.vanta.client.mod.hud.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;

/**
 * World information HUD elements
 */
public class WorldHudElements {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public static class MiniMap implements IHudElement {
        private int size = 128;
        private boolean northUp = false;
        private int zoom = 1;
        private boolean showMobs = false;
        private boolean showPlayers = false;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.world == null) return;
            
            // Background
            fill(matrices, x, y, x + size, y + size, 0x80000000);
            
            // Border
            drawRectOutline(matrices, x, y, x + size, y + size, 0xFF00FF87);
            
            // Player dot in center
            int centerX = x + size / 2;
            int centerY = y + size / 2;
            fill(matrices, centerX - 2, centerY - 2, centerX + 2, centerY + 2, 0xFF00FF87);
            
            // Direction indicator
            float yaw = mc.player.getYaw();
            if (!northUp) {
                // Draw arrow pointing in player direction
                int arrowX = centerX + (int)(Math.sin(Math.toRadians(yaw)) * 8);
                int arrowY = centerY - (int)(Math.cos(Math.toRadians(yaw)) * 8);
                fill(matrices, arrowX - 1, arrowY - 1, arrowX + 1, arrowY + 1, 0xFFFFFFFF);
            }
            
            // North label
            TextRenderer tr = mc.textRenderer;
            tr.draw(matrices, "N", x + 2, y + 2, 0xFFFFFF);
        }
        
        public void setNorthUp(boolean value) {
            northUp = value;
        }
        
        public void setZoom(int level) {
            zoom = MathHelper.clamp(level, 1, 4);
        }
        
        public int getZoom() {
            return zoom;
        }
        
        @Override
        public int getWidth() {
            return size;
        }
        
        @Override
        public int getHeight() {
            return size;
        }
        
        @Override
        public String getId() {
            return "minimap";
        }
        
        @Override
        public String getName() {
            return "Mini Map";
        }
    }
    
    public static class WaypointsList implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            // Placeholder - waypoints would be rendered from waypoint manager
            TextRenderer tr = mc.textRenderer;
            tr.draw(matrices, "Waypoints (Press INSERT to add)", x, y, 0xFFFF00);
        }
        
        @Override
        public int getWidth() {
            return 200;
        }
        
        @Override
        public int getHeight() {
            return 36;
        }
        
        @Override
        public String getId() {
            return "waypoints_list";
        }
        
        @Override
        public String getName() {
            return "Waypoints List";
        }
    }
    
    public static class ChunkCoordinates implements IHudElement {
        private boolean showSubChunk = false;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null) return;
            
            TextRenderer tr = mc.textRenderer;
            BlockPos pos = mc.player.getBlockPos();
            int chunkX = pos.getX() >> 4;
            int chunkZ = pos.getZ() >> 4;
            
            String text = String.format("Chunk: %d, %d", chunkX, chunkZ);
            if (showSubChunk) {
                int subX = pos.getX() & 15;
                int subZ = pos.getZ() & 15;
                text += String.format(" (%d, %d)", subX, subZ);
            }
            
            tr.draw(matrices, text, x, y, 0xAAAAAA);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("Chunk: -999999, -999999 (15, 15)");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "chunk_coordinates";
        }
        
        @Override
        public String getName() {
            return "Chunk Coordinates";
        }
    }
    
    public static class LightLevel implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            BlockPos pos = mc.player.getBlockPos();
            
            int blockLight = mc.world.getLightLevel(LightType.BLOCK, pos);
            int skyLight = mc.world.getLightLevel(LightType.SKY, pos);
            int totalLight = Math.max(blockLight, skyLight);
            
            boolean safe = totalLight > 7;
            int color = safe ? 0x00FF00 : 0xFF0000;
            
            String text = String.format("Light: %d (B:%d S:%d) %s", 
                totalLight, blockLight, skyLight, safe ? "✓" : "⚠");
            
            tr.draw(matrices, text, x, y, color);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("Light: 15 (B:15 S:15) ⚠");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "light_level";
        }
        
        @Override
        public String getName() {
            return "Light Level";
        }
    }
    
    public static class SlimeChunkIndicator implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.player == null || mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            BlockPos pos = mc.player.getBlockPos();
            
            // Simplified slime chunk check (actual implementation needs seed)
            boolean isSlimeChunk = isSlimeChunk(pos.getX(), pos.getZ());
            
            if (isSlimeChunk) {
                tr.draw(matrices, "🟢 Slime Chunk", x, y, 0x00FF00);
            }
        }
        
        private boolean isSlimeChunk(int chunkX, int chunkZ) {
            // Actual implementation would use world seed
            // This is a placeholder
            return false;
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("🟢 Slime Chunk");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "slime_chunk";
        }
        
        @Override
        public String getName() {
            return "Slime Chunk Indicator";
        }
    }
    
    public static class WeatherStatus implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            boolean raining = mc.world.isRaining();
            boolean thundering = mc.world.isThundering();
            
            String icon = "☀️";
            String text = "Clear";
            
            if (thundering) {
                icon = "⛈️";
                text = "Thunderstorm";
            } else if (raining) {
                icon = "🌧️";
                text = "Rain";
            }
            
            tr.draw(matrices, icon + " " + text, x, y, 0xAAAAAA);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("⛈️ Thunderstorm");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "weather_status";
        }
        
        @Override
        public String getName() {
            return "Weather Status";
        }
    }
    
    public static class MoonPhase implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            long time = mc.world.getTimeOfDay();
            int moonPhase = (int)(time / 24000 % 28);
            
            String[] phases = {
                "🌑", "🌒", "🌓", "🌔", "🌕", "🌖", "🌗", "🌘"
            };
            
            String phase = phases[moonPhase % 8];
            tr.draw(matrices, phase, x, y, 0xFFFFAA);
        }
        
        @Override
        public int getWidth() {
            return 16;
        }
        
        @Override
        public int getHeight() {
            return 16;
        }
        
        @Override
        public String getId() {
            return "moon_phase";
        }
        
        @Override
        public String getName() {
            return "Moon Phase";
        }
    }
    
    public static class DimensionInfo implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.world == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            RegistryKey<net.minecraft.world.World> dimension = mc.world.getRegistryKey();
            String dimName = dimension.getValue().getPath().toUpperCase();
            
            tr.draw(matrices, dimName, x, y, 0x00FF87);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("NETHER");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "dimension_info";
        }
        
        @Override
        public String getName() {
            return "Dimension Info";
        }
    }
    
    private static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        // Simplified fill implementation
    }
    
    private static void drawRectOutline(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        // Simplified outline implementation
    }
}
