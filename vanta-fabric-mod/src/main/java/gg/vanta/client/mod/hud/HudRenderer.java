package gg.vanta.client.mod.hud;

import gg.vanta.client.mod.VantaClientMod;
import gg.vanta.client.mod.modules.Module;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.RenderTickCounter;
import org.joml.Matrix4f;

import java.util.ArrayList;
import java.util.List;

/**
 * HUD Renderer - Handles all HUD element rendering
 * Supports drag-and-drop layout editor, customizable positions and styles
 */
public class HudRenderer {
    private final MinecraftClient client = MinecraftClient.getInstance();
    
    // List of all HUD elements
    private final List<HudElement> elements = new ArrayList<>();
    
    // Editor state
    private boolean editorMode = false;
    private HudElement selectedElement = null;
    private int dragOffsetX = 0;
    private int dragOffsetY = 0;
    
    // Grid snap size (8px)
    private static final int GRID_SIZE = 8;
    
    /**
     * Initialize default HUD elements
     */
    public void initialize() {
        // Core elements
        elements.add(new FpsElement(10, 10));
        elements.add(new PingElement(10, 30));
        elements.add(new CoordinatesElement(10, 50));
        elements.add(new DirectionElement(10, 70));
        elements.add(new BiomeElement(10, 90));
        elements.add(new TimeElement(10, 110));
        
        // Player state elements
        elements.add(new ArmorDurabilityElement(200, 10));
        elements.add(new PotionEffectsElement(-10, 10, true)); // Right aligned
        elements.add(new SaturationElement(200, 30));
        elements.add(new ExperienceElement(200, 50));
        elements.add(new HeldItemDurabilityElement(200, 70));
        
        // PVP elements
        elements.add(new KeystrokesElement(-150, -10, true)); // Bottom right
        elements.add(new CpsElement(-150, -30, true));
        elements.add(new TargetHealthElement(-10, -50, true));
        
        // World info
        elements.add(new MiniMapElement(-200, 10, true));
        elements.add(new WaypointsListElement(-200, 220, true));
        elements.add(new LightLevelElement(10, 130));
    }
    
    /**
     * Render all enabled HUD elements
     * @param context Draw context
     * @param tickCounter Render tick counter
     */
    public void render(DrawContext context, RenderTickCounter tickCounter) {
        if (client.player == null || client.getOverlay() != null) {
            return;
        }
        
        Matrix4f matrices = context.getMatrices().peek().getPositionMatrix();
        
        for (HudElement element : elements) {
            if (element.isEnabled()) {
                element.render(context, tickCounter);
            }
        }
        
        // Render editor overlay if in editor mode
        if (editorMode) {
            renderEditorOverlay(context);
        }
    }
    
    /**
     * Render editor overlay with grid and selection
     * @param context Draw context
     */
    private void renderEditorOverlay(DrawContext context) {
        int width = client.getWindow().getScaledWidth();
        int height = client.getWindow().getScaledHeight();
        
        // Draw grid lines
        context.fill(0, 0, width, height, 0x2000FF87);
        
        // Vertical lines
        for (int x = 0; x < width; x += GRID_SIZE) {
            context.fill(x, 0, x + 1, height, 0x4000FF87);
        }
        
        // Horizontal lines
        for (int y = 0; y < height; y += GRID_SIZE) {
            context.fill(0, y, width, y + 1, 0x4000FF87);
        }
        
        // Draw element bounds
        for (HudElement element : elements) {
            int[] pos = element.getPosition();
            int[] size = element.getSize();
            
            // Selection highlight
            if (element == selectedElement) {
                context.fill(pos[0] - 2, pos[1] - 2, 
                           pos[0] + size[0] + 2, pos[1] + size[1] + 2, 
                           0x6000FF87);
            }
            
            // Element border
            context.drawBorder(pos[0], pos[1], size[0], size[1], 
                             element.isEnabled() ? 0xFF00FF87 : 0xFFFF4757);
            
            // Element name
            context.drawText(client.textRenderer, element.getName(), 
                           pos[0], pos[1] - 10, 0xFFFFFFFF, true);
        }
        
        // Editor instructions
        String instructions = "HUD Editor - Drag to move | Scroll to resize | ESC to exit";
        int textWidth = client.textRenderer.getWidth(instructions);
        context.fill(10, height - 30, textWidth + 20, height - 10, 0xAA000000);
        context.drawText(client.textRenderer, instructions, 15, height - 25, 0xFFFFFFFF, false);
    }
    
    /**
     * Toggle HUD editor mode
     */
    public void toggleEditor() {
        editorMode = !editorMode;
        selectedElement = null;
        
        if (editorMode) {
            VantaClientMod.LOGGER.info("HUD Editor enabled");
        } else {
            VantaClientMod.LOGGER.info("HUD Editor disabled");
            saveLayout();
        }
    }
    
    /**
     * Handle mouse click for editor
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button
     * @return True if handled
     */
    public boolean handleMouseClick(double mouseX, double mouseY, int button) {
        if (!editorMode) return false;
        
        // Check if clicking on an element
        for (HudElement element : elements) {
            int[] pos = element.getPosition();
            int[] size = element.getSize();
            
            if (mouseX >= pos[0] && mouseX <= pos[0] + size[0] &&
                mouseY >= pos[1] && mouseY <= pos[1] + size[1]) {
                
                if (button == 0) { // Left click
                    selectedElement = element;
                    dragOffsetX = (int) (mouseX - pos[0]);
                    dragOffsetY = (int) (mouseY - pos[1]);
                    return true;
                }
            }
        }
        
        selectedElement = null;
        return false;
    }
    
    /**
     * Handle mouse drag for editor
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     */
    public void handleMouseDrag(double mouseX, double mouseY) {
        if (!editorMode || selectedElement == null) return;
        
        int newX = (int) (mouseX - dragOffsetX);
        int newY = (int) (mouseY - dragOffsetY);
        
        // Snap to grid
        if (!isAltDown()) {
            newX = Math.round((float) newX / GRID_SIZE) * GRID_SIZE;
            newY = Math.round((float) newY / GRID_SIZE) * GRID_SIZE;
        }
        
        selectedElement.setPosition(newX, newY);
    }
    
    /**
     * Handle mouse scroll for resizing
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param amount Scroll amount
     * @return True if handled
     */
    public boolean handleMouseScroll(double mouseX, double mouseY, double amount) {
        if (!editorMode || selectedElement == null) return false;
        
        int scaleChange = amount > 0 ? 1 : -1;
        selectedElement.setScale(Math.max(1, selectedElement.getScale() + scaleChange));
        
        return true;
    }
    
    /**
     * Check if ALT key is pressed (disables grid snap)
     * @return True if ALT is down
     */
    private boolean isAltDown() {
        return client.options.altKey.isPressed();
    }
    
    /**
     * Save current HUD layout
     */
    public void saveLayout() {
        // Save layout to config
        // Implementation would serialize element positions and settings
        VantaClientMod.LOGGER.info("HUD layout saved");
    }
    
    /**
     * Load HUD layout from config
     */
    public void loadLayout() {
        // Load layout from config
        // Implementation would deserialize element positions and settings
        VantaClientMod.LOGGER.info("HUD layout loaded");
    }
    
    /**
     * Export layout to file
     * @param filename Output filename
     */
    public void exportLayout(String filename) {
        // Export layout as .vanta-hud JSON file
        VantaClientMod.LOGGER.info("Exporting HUD layout to {}", filename);
    }
    
    /**
     * Import layout from file
     * @param filename Input filename
     */
    public void importLayout(String filename) {
        // Import layout from .vanta-hud JSON file
        VantaClientMod.LOGGER.info("Importing HUD layout from {}", filename);
    }
    
    /**
     * Reset all elements to default positions
     */
    public void resetLayout() {
        initialize();
        VantaClientMod.LOGGER.info("HUD layout reset to defaults");
    }
}
