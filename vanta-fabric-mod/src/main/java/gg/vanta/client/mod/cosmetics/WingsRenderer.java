package gg.vanta.client.mod.cosmetics;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.RotationAxis;

public class WingsRenderer {
    private static boolean enabled = false;
    private static float wingAngle = 0.0f;
    private static float prevWingAngle = 0.0f;
    
    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                             LivingEntity entity, float animationProgress,
                             float bodyYaw, boolean isSneaking, boolean isJumping) {
        if (!enabled) return;
        
        matrices.push();
        
        // Position at player's back
        matrices.translate(0.0, 0.25, 0.1);
        
        // Rotate with player
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        
        // Fold wings when sneaking
        float sneakFold = isSneaking ? 0.5f : 0.0f;
        
        // Flap wings when jumping
        if (isJumping) {
            wingAngle += 0.3f;
        } else {
            wingAngle *= 0.9f;
        }
        
        // Render left wing
        renderWing(matrices, vertexConsumers, true, wingAngle, sneakFold, animationProgress);
        
        // Render right wing
        renderWing(matrices, vertexConsumers, false, wingAngle, sneakFold, animationProgress);
        
        matrices.pop();
    }
    
    private static void renderWing(MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                  boolean isLeft, float angle, float sneakFold, float animationProgress) {
        matrices.push();
        
        // Translate to wing position
        matrices.translate(isLeft ? -0.3 : 0.3, 0.0, 0.0);
        
        // Wing rotation
        float baseAngle = isLeft ? 30.0f : -30.0f;
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(baseAngle + angle * (isLeft ? 1 : -1)));
        
        // Apply sneak fold
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(sneakFold * 45.0f));
        
        // Add subtle wave animation
        float wave = (float)Math.sin(animationProgress * 0.3f) * 5.0f;
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(wave * (isLeft ? 1 : -1)));
        
        // Scale wing
        matrices.scale(0.4f, 0.4f, 0.4f);
        
        // Get vertex consumer
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(
            net.minecraft.client.MinecraftClient.getInstance().getEntityRenderDispatcher().getTexturedRenderLayers().getEntityTranslucent());
        
        // Simple wing shape (triangle fan)
        int color = 255 << 24 | 0x00FF87; // VANTA green
        
        // Wing vertices
        float u0 = isLeft ? 0.0f : 0.5f;
        float u1 = isLeft ? 0.5f : 1.0f;
        
        // Draw wing quad
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 0.0f, 0.0f, 0.0f)
                     .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255)
                     .texture(u0, 0.0f)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 0.0f, -0.5f, 0.3f)
                     .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255)
                     .texture(u0, 1.0f)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 0.5f, -0.3f, 0.2f)
                     .color((color >> 16) & 255, (color >> 8) & 255, color & 255, (color >> 24) & 255)
                     .texture(u1, 0.7f)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        matrices.pop();
    }
    
    public static void setEnabled(boolean enabled) {
        WingsRenderer.enabled = enabled;
    }
    
    public static boolean isEnabled() {
        return enabled;
    }
}
