package gg.vanta.client.mod.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import gg.vanta.client.mod.VantaClientMod;
import gg.vanta.client.mod.config.VantaConfig;
import gg.vanta.client.mod.gui.modules.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.GameRenderer;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class ClickGuiScreen extends Screen {
    private final ModuleManager moduleManager;
    private Category selectedCategory = Category.HUD;
    private int scrollOffset = 0;
    private int rightPanelScroll = 0;
    
    public ClickGuiScreen() {
        super(Text.literal("VANTA Click GUI"));
        this.moduleManager = ModuleManager.get();
    }
    
    @Override
    protected void init() {
        super.init();
    }
    
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Blur background
        renderBlurBackground(context);
        
        // Dark overlay
        context.fill(0, 0, width, height, 0xE6000000);
        
        // Scanline effect (subtle)
        renderScanlines(context);
        
        // Watermark
        context.drawTextWithShadow(MinecraftClient.getInstance().textRenderer, 
            "VANTA", width - 100, height - 20, 0x0500FF87);
        
        // Left sidebar - Categories
        renderCategories(context, mouseX, mouseY);
        
        // Center - Module grid
        renderModuleGrid(context, mouseX, mouseY, delta);
        
        // Right panel - Settings (if module selected)
        if (moduleManager.getSelectedModule() != null) {
            renderSettingsPanel(context, mouseX, mouseY, delta);
        }
        
        // Top bar - Search + Profile
        renderTopBar(context, mouseX, mouseY);
        
        super.render(context, mouseX, mouseY, delta);
    }
    
    private void renderBlurBackground(DrawContext context) {
        // In actual implementation, this would use a shader for Gaussian blur
        // For now, we'll just darken significantly
        context.fill(0, 0, width, height, 0x40000000);
    }
    
    private void renderScanlines(DrawContext context) {
        for (int y = 0; y < height; y += 2) {
            context.fill(0, y, width, y + 1, 0x08FFFFFF);
        }
    }
    
    private void renderCategories(DrawContext context, int mouseX, int mouseY) {
        int sidebarWidth = 200;
        int x = 20;
        int y = 60;
        
        // Background panel
        context.fill(x - 10, 50, x + sidebarWidth + 10, height - 20, 0x99000000);
        
        VantaConfig.ConfigData data = VantaConfig.get().data;
        Category[] categories = Category.values();
        
        for (int i = 0; i < categories.length; i++) {
            Category cat = categories[i];
            int catY = y + (i * 35);
            boolean isSelected = selectedCategory == cat;
            boolean isHovered = mouseX >= x && mouseX <= x + sidebarWidth && 
                               mouseY >= catY && mouseY <= catY + 30;
            
            // Hover background
            if (isHovered) {
                context.fill(x - 8, catY, x + sidebarWidth + 8, catY + 30, 0x3300FF87);
            }
            
            // Active indicator
            if (isSelected) {
                context.fill(x - 10, catY, x - 6, catY + 30, 0xFF00FF87);
                // Glow effect
                context.fill(x - 12, catY, x - 10, catY + 30, 0x4400FF87);
            }
            
            // Category name
            String name = cat.displayName;
            int color = isSelected ? 0xFF00FF87 : (isHovered ? 0xFF00FF87 : 0xFFFFFFFF);
            context.drawTextWithShadow(textRenderer, name, x + 15, catY + 10, color);
        }
    }
    
    private void renderModuleGrid(DrawContext context, int mouseX, int mouseY, float delta) {
        int startX = 240;
        int startY = 60;
        int cardWidth = 280;
        int cardHeight = 100;
        int gap = 15;
        int columns = 2;
        
        var modules = moduleManager.getModulesForCategory(selectedCategory);
        int rows = (modules.size() + columns - 1) / columns;
        int gridHeight = rows * (cardHeight + gap);
        
        // Scrollable area
        int maxScroll = Math.max(0, gridHeight - (height - 100));
        scrollOffset = Math.clamp(scrollOffset, 0, maxScroll);
        
        // Background panel
        context.fill(startX - 10, 50, width - 360, height - 20, 0x66000000);
        
        int col = 0;
        int row = 0;
        
        for (var module : modules) {
            int x = startX + (col * (cardWidth + gap));
            int y = startY + (row * (cardHeight + gap)) - scrollOffset;
            
            if (y > height - 40) continue; // Don't render if scrolled out
            
            boolean isEnabled = module.isEnabled();
            boolean isHovered = mouseX >= x && mouseX <= x + cardWidth && 
                               mouseY >= y && mouseY <= y + cardHeight;
            
            // Card background with glassmorphism
            int bgColor = isEnabled ? 0x4400FF87 : 0x44333333;
            if (isHovered) {
                bgColor = isEnabled ? 0x6600FF87 : 0x66444444;
            }
            context.fill(x, y, x + cardWidth, y + cardHeight, bgColor);
            
            // Border glow when enabled
            if (isEnabled) {
                context.hline(x, x + cardWidth, y, 0x6600FF87);
                context.hline(x, x + cardWidth, y + cardHeight, 0x6600FF87);
                context.vline(x, y, y + cardHeight, 0x6600FF87);
                context.vline(x + cardWidth, y, y + cardHeight, 0x6600FF87);
            }
            
            // Module name
            context.drawTextWithShadow(textRenderer, module.getName(), x + 12, y + 10, 
                isEnabled ? 0xFF00FF87 : 0xFFFFFFFF);
            
            // Description (2 lines max)
            String desc = module.getDescription();
            if (desc.length() > 45) desc = desc.substring(0, 42) + "...";
            context.drawTextWithShadow(textRenderer, desc, x + 12, y + 28, 0xFFAAAAAA);
            
            // Toggle switch
            int toggleX = x + cardWidth - 50;
            int toggleY = y + cardHeight - 25;
            renderToggleSwitch(context, toggleX, toggleY, isEnabled);
            
            // Keybind chip
            String keybind = module.getKeybindName();
            int keybindWidth = textRenderer.getWidth(keybind) + 16;
            context.fill(toggleX - keybindWidth - 10, toggleY + 2, 
                        toggleX - 5, toggleY + 18, 0xFF333333);
            context.drawTextWithShadow(textRenderer, keybind, 
                toggleX - keybindWidth - 6, toggleY + 5, 0xFFCCCCCC);
            
            // Gear icon (settings)
            context.drawTextWithShadow(textRenderer, "⚙", x + cardWidth - 25, y + 10, 0xFF888888);
            
            col++;
            if (col >= columns) {
                col = 0;
                row++;
            }
        }
    }
    
    private void renderToggleSwitch(DrawContext context, int x, int y, boolean enabled) {
        int width = 40;
        int height = 18;
        
        // Background
        context.fill(x, y, x + width, y + height, enabled ? 0xFF00FF87 : 0xFF444444);
        
        // Thumb
        int thumbX = enabled ? x + width - 16 : x + 2;
        context.fill(thumbX, y + 2, thumbX + 14, y + height - 2, 0xFFFFFFFF);
        
        // Glow when enabled
        if (enabled) {
            context.fill(x - 2, y - 2, x + width + 2, y + height + 2, 0x3300FF87);
        }
    }
    
    private void renderSettingsPanel(DrawContext context, int mouseX, int mouseY, float delta) {
        var module = moduleManager.getSelectedModule();
        if (module == null) return;
        
        int panelX = width - 360;
        int panelY = 50;
        int panelWidth = 350;
        
        // Background
        context.fill(panelX, panelY, width - 10, height - 20, 0x99000000);
        
        // Header
        context.drawTextWithShadow(textRenderer, module.getName(), panelX + 15, panelY + 15, 0xFF00FF87);
        context.drawTextWithShadow(textRenderer, module.getDescription(), panelX + 15, panelY + 30, 0xFF888888);
        
        // Render module-specific settings
        int settingY = panelY + 60;
        for (var setting : module.getSettings()) {
            settingY = setting.render(context, panelX + 15, settingY, mouseX, mouseY, delta);
            settingY += 10;
        }
        
        // Reset button
        int resetY = height - 50;
        context.fill(panelX + 15, resetY, panelX + 150, resetY + 30, 0xFF444444);
        context.drawTextWithShadow(textRenderer, "Reset to Default", panelX + 25, resetY + 10, 0xFFFFFFFF);
    }
    
    private void renderTopBar(DrawContext context, int mouseX, int mouseY) {
        // Top bar background
        context.fill(0, 0, width, 50, 0xCC000000);
        
        // Search bar
        context.fill(200, 10, 450, 40, 0xFF222222);
        context.drawTextWithShadow(textRenderer, "Search modules... (Ctrl+F)", 210, 18, 0xFF666666);
        
        // Profile switcher
        context.fill(width - 200, 10, width - 20, 40, 0xFF333333);
        context.drawTextWithShadow(textRenderer, "Profile: Default ▼", width - 190, 18, 0xFFFFFFFF);
    }
    
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == GLFW.GLFW_MOUSE_BUTTON_LEFT) {
            // Check category clicks
            if (mouseX >= 10 && mouseX <= 210 && mouseY >= 60) {
                Category[] categories = Category.values();
                for (int i = 0; i < categories.length; i++) {
                    int catY = 60 + (i * 35);
                    if (mouseY >= catY && mouseY <= catY + 30) {
                        selectedCategory = categories[i];
                        scrollOffset = 0;
                        return true;
                    }
                }
            }
            
            // Check module toggles
            // (Implementation would check each module card's toggle area)
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }
    
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        if (mouseX >= 240 && mouseX <= width - 370) {
            scrollOffset -= (int)(verticalAmount * 15);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, horizontalAmount, verticalAmount);
    }
    
    @Override
    public boolean shouldPause() {
        return false;
    }
    
    public enum Category {
        HUD("HUD"),
        RENDER("Render"),
        WORLD("World"),
        PLAYER("Player"),
        SOCIAL("Social"),
        COSMETICS("Cosmetics"),
        PERFORMANCE("Performance");
        
        public final String displayName;
        
        Category(String displayName) {
            this.displayName = displayName;
        }
    }
}
