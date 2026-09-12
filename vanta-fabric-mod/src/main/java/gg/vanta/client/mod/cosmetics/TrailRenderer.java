package gg.vanta.client.mod.cosmetics;

import net.minecraft.particle.ParticleTypes;

public class TrailRenderer {
    public enum TrailType {
        NONE, GREEN_SPARKLE, SHADOW_WISPS, EMBER, VOID_PARTICLES, RAINBOW
    }
    
    private static TrailType currentTrail = TrailType.NONE;
    
    public static void renderTrail(double x, double y, double z, net.minecraft.world.World world) {
        if (currentTrail == TrailType.NONE) return;
        
        switch (currentTrail) {
            case GREEN_SPARKLE -> spawnGreenSparkle(world, x, y, z);
            case SHADOW_WISPS -> spawnShadowWisps(world, x, y, z);
            case EMBER -> spawnEmber(world, x, y, z);
            case VOID_PARTICLES -> spawnVoidParticles(world, x, y, z);
            case RAINBOW -> spawnRainbowTrail(world, x, y, z);
            default -> {}
        }
    }
    
    private static void spawnGreenSparkle(net.minecraft.world.World world, double x, double y, double z) {
        if (world.isClient) {
            for (int i = 0; i < 2; i++) {
                world.addParticle(
                    ParticleTypes.HAPPY_VILLAGER,
                    x + (Math.random() - 0.5) * 0.3,
                    y + 0.1 + Math.random() * 0.2,
                    z + (Math.random() - 0.5) * 0.3,
                    0.0, 0.02, 0.0
                );
            }
        }
    }
    
    private static void spawnShadowWisps(net.minecraft.world.World world, double x, double y, double z) {
        if (world.isClient) {
            for (int i = 0; i < 3; i++) {
                world.addParticle(
                    ParticleTypes.PORTAL,
                    x + (Math.random() - 0.5) * 0.4,
                    y + Math.random() * 0.3,
                    z + (Math.random() - 0.5) * 0.4,
                    (Math.random() - 0.5) * 0.1,
                    0.05,
                    (Math.random() - 0.5) * 0.1
                );
            }
        }
    }
    
    private static void spawnEmber(net.minecraft.world.World world, double x, double y, double z) {
        if (world.isClient) {
            for (int i = 0; i < 4; i++) {
                world.addParticle(
                    ParticleTypes.FLAME,
                    x + (Math.random() - 0.5) * 0.3,
                    y + 0.1 + Math.random() * 0.2,
                    z + (Math.random() - 0.5) * 0.3,
                    (Math.random() - 0.5) * 0.05,
                    0.08,
                    (Math.random() - 0.5) * 0.05
                );
            }
        }
    }
    
    private static void spawnVoidParticles(net.minecraft.world.World world, double x, double y, double z) {
        if (world.isClient) {
            for (int i = 0; i < 3; i++) {
                world.addParticle(
                    ParticleTypes.DRAGON_BREATH,
                    x + (Math.random() - 0.5) * 0.4,
                    y + Math.random() * 0.3,
                    z + (Math.random() - 0.5) * 0.4,
                    0.0, 0.03, 0.0
                );
            }
        }
    }
    
    private static void spawnRainbowTrail(net.minecraft.world.World world, double x, double y, double z) {
        if (world.isClient) {
            // Cycle through particle types for rainbow effect
            int time = (int)(System.currentTimeMillis() / 500) % 5;
            var particle = switch (time) {
                case 0 -> ParticleTypes.HAPPY_VILLAGER;
                case 1 -> ParticleTypes.HEART;
                case 2 -> ParticleTypes.NOTE;
                case 3 -> ParticleTypes.CLOUD;
                default -> ParticleTypes.END_ROD;
            };
            
            for (int i = 0; i < 2; i++) {
                world.addParticle(
                    particle,
                    x + (Math.random() - 0.5) * 0.3,
                    y + 0.1 + Math.random() * 0.2,
                    z + (Math.random() - 0.5) * 0.3,
                    0.0, 0.02, 0.0
                );
            }
        }
    }
    
    public static void setTrailType(TrailType type) {
        currentTrail = type;
    }
    
    public static TrailType getTrailType() {
        return currentTrail;
    }
}
