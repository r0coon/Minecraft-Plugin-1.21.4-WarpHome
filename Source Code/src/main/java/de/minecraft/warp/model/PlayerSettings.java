package de.minecraft.warp.model;

import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.Particle;

public class PlayerSettings {
    
    private String bedColor;
    private String particleType;
    private String particleColor;
    private boolean soundEnabled;
    
    public PlayerSettings() {
        this.bedColor = "RED";
        this.particleType = "STANDARD";
        this.particleColor = "BLUE";  // Blau statt Lila
        this.soundEnabled = true;
    }
    
    public Material getBedMaterial() {
        return switch (bedColor) {
            case "WHITE" -> Material.WHITE_BED;
            case "ORANGE" -> Material.ORANGE_BED;
            case "MAGENTA" -> Material.MAGENTA_BED;
            case "LIGHT_BLUE" -> Material.LIGHT_BLUE_BED;
            case "YELLOW" -> Material.YELLOW_BED;
            case "LIME" -> Material.LIME_BED;
            case "PINK" -> Material.PINK_BED;
            case "GRAY" -> Material.GRAY_BED;
            case "LIGHT_GRAY" -> Material.LIGHT_GRAY_BED;
            case "CYAN" -> Material.CYAN_BED;
            case "PURPLE" -> Material.PURPLE_BED;
            case "BLUE" -> Material.BLUE_BED;
            case "BROWN" -> Material.BROWN_BED;
            case "GREEN" -> Material.GREEN_BED;
            case "BLACK" -> Material.BLACK_BED;
            default -> Material.RED_BED;
        };
    }
    
    public Particle getParticle() {
        return switch (particleType) {
            case "HEART" -> Particle.HEART;
            case "FLAME" -> Particle.FLAME;
            case "SOUL" -> Particle.SOUL;
            case "DRAGON" -> Particle.DRAGON_BREATH;
            case "MAGIC" -> Particle.ENCHANTMENT_TABLE;
            case "SPARKLE" -> Particle.FIREWORKS_SPARK;
            default -> Particle.END_ROD;
        };
    }
    
    public Color getParticleColor() {
        return switch (particleColor) {
            case "RED" -> Color.fromRGB(255, 0, 0);
            case "GREEN" -> Color.fromRGB(0, 255, 0);
            case "BLUE" -> Color.fromRGB(100, 200, 255);  // Hellblau / Aqua
            case "PURPLE" -> Color.fromRGB(200, 0, 255);
            case "PINK" -> Color.fromRGB(255, 100, 200);
            case "YELLOW" -> Color.fromRGB(255, 255, 0);
            case "CYAN" -> Color.fromRGB(0, 255, 255);
            case "WHITE" -> Color.fromRGB(255, 255, 255);
            case "RAINBOW" -> Color.fromRGB(255, 0, 255); // Spezial
            default -> Color.fromRGB(100, 200, 255);  // Hellblau als Standard
        };
    }
    
    public String getBedColor() {
        return bedColor;
    }
    
    public void setBedColor(String bedColor) {
        this.bedColor = bedColor;
    }
    
    public String getParticleType() {
        return particleType;
    }
    
    public void setParticleType(String particleType) {
        this.particleType = particleType;
    }
    
    public String getParticleColorName() {
        return particleColor;
    }
    
    public void setParticleColor(String particleColor) {
        this.particleColor = particleColor;
    }
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }
}

