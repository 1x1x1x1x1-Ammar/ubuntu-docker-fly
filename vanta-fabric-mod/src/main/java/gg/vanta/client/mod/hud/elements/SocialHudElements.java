package gg.vanta.client.mod.hud.elements;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.Team;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Social and stream related HUD elements
 */
public class SocialHudElements {
    private static final MinecraftClient mc = MinecraftClient.getInstance();
    
    public static class Scoreboard implements IHudElement {
        private boolean customFont = true;
        private boolean animatedChanges = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.world == null) return;
            
            Scoreboard scoreboard = mc.world.getScoreboard();
            ScoreboardObjective objective = scoreboard.getObjectiveForSlot(ScoreboardDisplaySlot.SIDEBAR);
            
            if (objective == null) return;
            
            TextRenderer tr = mc.textRenderer;
            
            // Render objective name
            String title = objective.getDisplayName().getString();
            tr.draw(matrices, title, x, y, 0xFFFFFF);
            
            // Render scores
            List<String> scores = getSidebarScores(scoreboard, objective);
            for (int i = 0; i < scores.size() && i < 15; i++) {
                String score = scores.get(i);
                tr.draw(matrices, score, x, y + 10 + i * 10, 0xAAAAAA);
            }
        }
        
        private List<String> getSidebarScores(Scoreboard scoreboard, ScoreboardObjective objective) {
            List<String> result = new ArrayList<>();
            
            for (var entry : scoreboard.getSortedScoreNames(objective)) {
                int score = scoreboard.getScore(entry, objective).score();
                String displayName = entry.getName();
                result.add(String.format("%s: %d", displayName, score));
            }
            
            return result;
        }
        
        @Override
        public int getWidth() {
            return 150;
        }
        
        @Override
        public int getHeight() {
            return 160;
        }
        
        @Override
        public String getId() {
            return "scoreboard";
        }
        
        @Override
        public String getName() {
            return "Scoreboard";
        }
    }
    
    public static class TabListCustom implements IHudElement {
        private boolean showPing = true;
        private boolean showHead = true;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            // This would normally be rendered in the tab screen
            // Placeholder for custom tab list styling
            TextRenderer tr = mc.textRenderer;
            tr.draw(matrices, "[TAB] Custom Tab List", x, y, 0x00FF87);
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
            return "tablist_custom";
        }
        
        @Override
        public String getName() {
            return "Custom Tab List";
        }
    }
    
    public static class ChatTimestamps implements IHudElement {
        private String timestampFormat = "HH:mm";
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            // This is more of a chat modification than a HUD element
            // Placeholder to indicate the feature exists
            TextRenderer tr = mc.textRenderer;
            tr.draw(matrices, "[Chat Timestamps Enabled]", x, y, 0x888888);
        }
        
        @Override
        public int getWidth() {
            return 150;
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "chat_timestamps";
        }
        
        @Override
        public String getName() {
            return "Chat Timestamps";
        }
    }
    
    public static class DeathMessageHighlighter implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            // This modifies chat rendering, not a visible HUD element
            TextRenderer tr = mc.textRenderer;
            tr.draw(matrices, "[Death Highlighter Active]", x, y, 0xFF0000);
        }
        
        @Override
        public int getWidth() {
            return 140;
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "death_highlighter";
        }
        
        @Override
        public String getName() {
            return "Death Message Highlighter";
        }
    }
    
    public static class PlayerCount implements IHudElement {
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            if (mc.getNetworkHandler() == null) return;
            
            TextRenderer tr = mc.textRenderer;
            int playerCount = mc.getNetworkHandler().getPlayerList().size();
            
            String text = String.format("Players: %d", playerCount);
            tr.draw(matrices, text, x, y, 0x00FF87);
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("Players: 999");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "player_count";
        }
        
        @Override
        public String getName() {
            return "Player Count";
        }
    }
    
    public static class ServerTPS implements IHudElement {
        private double tps = 20.0;
        
        @Override
        public void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y) {
            TextRenderer tr = mc.textRenderer;
            
            int color = tps >= 19.0 ? 0x00FF00 : tps >= 15.0 ? 0xFFFF00 : 0xFF0000;
            String text = String.format("TPS: %.1f", tps);
            
            tr.draw(matrices, text, x, y, color);
        }
        
        public void updateTPS(double value) {
            tps = value;
        }
        
        @Override
        public int getWidth() {
            return mc.textRenderer.getWidth("TPS: 20.0");
        }
        
        @Override
        public int getHeight() {
            return 9;
        }
        
        @Override
        public String getId() {
            return "server_tps";
        }
        
        @Override
        public String getName() {
            return "Server TPS";
        }
    }
    
    private static void fill(MatrixStack matrices, int x1, int y1, int x2, int y2, int color) {
        // Simplified fill implementation
    }
}
