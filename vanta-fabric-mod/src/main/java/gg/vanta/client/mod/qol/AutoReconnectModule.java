package gg.vanta.client.mod.qol;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;

public class AutoReconnectModule {
    private static boolean enabled = true;
    private static int countdownSeconds = 5;
    private static int currentCountdown = 0;
    private static boolean reconnecting = false;
    private static String lastServerAddress = null;
    private static int retryAttempts = 0;
    private static final int MAX_RETRIES = 3;
    
    public static void onDisconnect(Text reason) {
        if (!enabled || reconnecting) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        ClientPlayNetworkHandler handler = mc.getNetworkHandler();
        
        if (handler != null && handler.getConnection() != null) {
            // Store last server address
            lastServerAddress = handler.getConnection().getAddress().toString();
        }
        
        if (lastServerAddress != null && retryAttempts < MAX_RETRIES) {
            reconnecting = true;
            currentCountdown = countdownSeconds;
        }
    }
    
    public static void tick() {
        if (!reconnecting) return;
        
        currentCountdown--;
        
        if (currentCountdown <= 0) {
            attemptReconnect();
        }
    }
    
    private static void attemptReconnect() {
        MinecraftClient mc = MinecraftClient.getInstance();
        
        if (lastServerAddress != null) {
            retryAttempts++;
            mc.execute(() -> {
                try {
                    String[] parts = lastServerAddress.split(":");
                    String host = parts[0].replace("/", "");
                    int port = parts.length > 1 ? Integer.parseInt(parts[1]) : 25565;
                    
                    mc.disconnect();
                    mc.connectToServer(host, port);
                    
                    reconnecting = false;
                    retryAttempts = 0;
                } catch (Exception e) {
                    e.printStackTrace();
                    if (retryAttempts < MAX_RETRIES) {
                        currentCountdown = countdownSeconds;
                    } else {
                        reconnecting = false;
                    }
                }
            });
        }
    }
    
    public static void cancel() {
        reconnecting = false;
        retryAttempts = 0;
    }
    
    public static boolean isReconnecting() {
        return reconnecting;
    }
    
    public static int getCountdown() {
        return currentCountdown;
    }
    
    public static void setEnabled(boolean enabled) {
        AutoReconnectModule.enabled = enabled;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
    
    public static void setDelay(int seconds) {
        countdownSeconds = Math.clamp(seconds, 1, 60);
    }
    
    public static int getDelay() {
        return countdownSeconds;
    }
}
