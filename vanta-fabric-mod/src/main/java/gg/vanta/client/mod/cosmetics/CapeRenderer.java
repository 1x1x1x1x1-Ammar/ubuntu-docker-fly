package gg.vanta.client.mod.cosmetics;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RotationAxis;

public class CapeRenderer {
    private static final Identifier DEFAULT_CAPE = new Identifier("vanta", "textures/capes/vanta_default.png");
    private static final Identifier PHANTOM_CAPE = new Identifier("vanta", "textures/capes/phantom.png");
    private static final Identifier EMERALD_CAPE = new Identifier("vanta", "textures/capes/emerald.png");
    private static final Identifier VOID_CAPE = new Identifier("vanta", "textures/capes/void.png");
    
    public enum CapeType {
        VANTA_DEFAULT, PHANTOM, EMERALD, VOID, CUSTOM
    }
    
    private static CapeType equippedCape = CapeType.VANTA_DEFAULT;
    private static float capeAngle = 0.0f;
    private static float prevCapeAngle = 0.0f;
    
    public static void render(MatrixStack matrices, VertexConsumerProvider vertexConsumers, 
                             LivingEntity entity, float animationProgress, 
                             float headYaw, float bodyYaw) {
        if (equippedCape == null) return;
        
        MinecraftClient mc = MinecraftClient.getInstance();
        Identifier capeTexture = getCapeTexture(equippedCape);
        
        if (capeTexture == null || !mc.getTextureManager().getTexture(capeTexture).getGlId() > 0) return;
        
        matrices.push();
        
        // Position at player's back
        matrices.translate(0.0, 0.0, 0.25);
        
        // Rotate with player body
        matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(180.0f - bodyYaw));
        
        // Animate cape physics
        updateCapePhysics(entity, animationProgress);
        matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(capeAngle));
        
        // Scale to cape size (64x32 texture -> 0.5 x 0.25 blocks)
        matrices.scale(0.5f, 0.5f, 0.5f);
        
        // Render cape
        VertexConsumer vertexConsumer = vertexConsumers.getBuffer(mc.getEntityRenderDispatcher().getTexturedRenderLayers().getText(capeTexture));
        
        // Draw cape rectangle with UV mapping
        float u0 = 0.0f;
        float u1 = 1.0f;
        float v0 = 0.0f;
        float v1 = 1.0f;
        
        // Front face
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), -0.5f, 0.0f, -0.25f)
                     .color(255, 255, 255, 255)
                     .texture(u0, v0)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 0.5f, 0.0f, -0.25f)
                     .color(255, 255, 255, 255)
                     .texture(u1, v0)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), 0.5f, -1.0f, -0.25f)
                     .color(255, 255, 255, 255)
                     .texture(u1, v1)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        vertexConsumer.vertex(matrices.peek().getPositionMatrix(), -0.5f, -1.0f, -0.25f)
                     .color(255, 255, 255, 255)
                     .texture(u0, v1)
                     .overlay(0)
                     .light(0xF000F0)
                     .normal(matrices.peek().getNormalMatrix(), 0.0f, 0.0f, 1.0f);
        
        matrices.pop();
    }
    
    private static void updateCapePhysics(LivingEntity entity, float animationProgress) {
        prevCapeAngle = capeAngle;
        
        // Calculate target angle based on movement
        float dx = (float)(entity.getX() - entity.prevX);
        float dz = (float)(entity.getZ() - entity.prevZ);
        float speed = MathHelper.sqrt(dx * dx + dz * dz);
        
        // Target angle based on speed
        float targetAngle = Math.min(speed * 10.0f, 45.0f);
        
        // Add wave effect
        float wave = (float)Math.sin(animationProgress * 0.5f) * 3.0f;
        
        // Smooth interpolation
        capeAngle += (targetAngle + wave - capeAngle) * 0.1f;
        
        // Clamp angle
        capeAngle = MathHelper.clamp(capeAngle, -30.0f, 60.0f);
    }
    
    private static Identifier getCapeTexture(CapeType type) {
        return switch (type) {
            case PHANTOM -> PHANTOM_CAPE;
            case EMERALD -> EMERALD_CAPE;
            case VOID -> VOID_CAPE;
            default -> DEFAULT_CAPE;
        };
    }
    
    public static void setCapeType(CapeType type) {
        equippedCape = type;
    }
    
    public static CapeType getCapeType() {
        return equippedCape;
    }
}
