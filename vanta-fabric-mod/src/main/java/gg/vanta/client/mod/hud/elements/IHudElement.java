package gg.vanta.client.mod.hud.elements;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;

/**
 * Interface for all HUD elements in VANTA Client
 */
public interface IHudElement {
    
    /**
     * Render the HUD element at the specified position
     * @param matrices Matrix stack for transformations
     * @param vertices Vertex consumer provider for rendering
     * @param x X position on screen
     * @param y Y position on screen
     */
    void render(MatrixStack matrices, VertexConsumerProvider vertices, int x, int y);
    
    /**
     * Get the width of this element in pixels
     * @return Width in pixels
     */
    int getWidth();
    
    /**
     * Get the height of this element in pixels
     * @return Height in pixels
     */
    int getHeight();
    
    /**
     * Get the unique identifier for this element
     * @return Element ID (e.g., "fps_counter")
     */
    String getId();
    
    /**
     * Get the display name for this element
     * @return Human-readable name (e.g., "FPS Counter")
     */
    String getName();
    
    /**
     * Check if this element should be visible
     * Default implementation returns true
     * @return Whether element is visible
     */
    default boolean isVisible() {
        return true;
    }
    
    /**
     * Called when the element is clicked
     * Default implementation does nothing
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param button Mouse button (0=left, 1=right)
     * @return True if click was handled
     */
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }
}
