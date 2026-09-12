package gg.vanta.client.mod;

import gg.vanta.client.mod.config.VantaConfig;
import gg.vanta.client.mod.hud.HudRenderer;
import gg.vanta.client.mod.modules.ModuleManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * VANTA Client Mod - Main initializer class
 * A feature-rich Fabric client mod with HUD, QoL modules, and cosmetics
 */
public class VantaClientMod implements ClientModInitializer {
    public static final String MOD_ID = "vanta";
    public static final String MOD_NAME = "VANTA Client";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    
    // Config instance
    public static VantaConfig config;
    
    // Module manager
    public static ModuleManager moduleManager;
    
    // HUD renderer
    public static HudRenderer hudRenderer;
    
    // Keybindings
    public static KeyBinding openGuiKey;
    public static KeyBinding toggleHudEditorKey;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Initializing {}...", MOD_NAME);
        
        // Initialize config
        config = VantaConfig.getInstance();
        config.load();
        
        // Initialize module manager
        moduleManager = new ModuleManager();
        moduleManager.initialize();
        
        // Initialize HUD renderer
        hudRenderer = new HudRenderer();
        HudRenderCallback.EVENT.register(hudRenderer::render);
        
        // Register keybindings
        registerKeybindings();
        
        // Register tick events
        ClientTickEvents.END_CLIENT_TICK.register(this::onClientTick);
        
        LOGGER.info("{} initialized successfully!", MOD_NAME);
    }
    
    /**
     * Register all keybindings
     */
    private void registerKeybindings() {
        // Open GUI key (Right Shift)
        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.vanta.open_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.vanta"
        ));
        
        // Toggle HUD editor key (H)
        toggleHudEditorKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.vanta.toggle_hud_editor",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.vanta"
        ));
    }
    
    /**
     * Handle client tick events
     * @param client Minecraft client instance
     */
    private void onClientTick(net.minecraft.client.MinecraftClient client) {
        // Handle GUI open key
        while (openGuiKey.wasPressed()) {
            if (client.player != null) {
                // Open Click GUI screen
                // client.setScreen(new ClickGUIScreen());
            }
        }
        
        // Handle HUD editor toggle
        while (toggleHudEditorKey.wasPressed()) {
            if (client.player != null) {
                hudRenderer.toggleEditor();
            }
        }
        
        // Update modules
        moduleManager.tick();
    }
    
    /**
     * Get the mod config
     * @return VantaConfig instance
     */
    public static VantaConfig getConfig() {
        return config;
    }
    
    /**
     * Get the module manager
     * @return ModuleManager instance
     */
    public static ModuleManager getModuleManager() {
        return moduleManager;
    }
    
    /**
     * Get the HUD renderer
     * @return HudRenderer instance
     */
    public static HudRenderer getHudRenderer() {
        return hudRenderer;
    }
}
