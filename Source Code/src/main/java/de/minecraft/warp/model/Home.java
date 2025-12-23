package de.minecraft.warp.model;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.UUID;

public class Home {

    private final String name;
    private final UUID owner;
    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private String bedColor;
    private String icon;
    private String iconVariant;

    public Home(String name, UUID owner, Location location) {
        this.name = name;
        this.owner = owner;
        this.worldName = location.getWorld().getName();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
        this.yaw = location.getYaw();
        this.pitch = location.getPitch();
        this.bedColor = "RED";
        this.icon = "BED";
        this.iconVariant = "DEFAULT";
    }

    public Home(String name, UUID owner, String worldName, double x, double y, double z, float yaw, float pitch, String bedColor, String icon, String iconVariant) {
        this.name = name;
        this.owner = owner;
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.bedColor = bedColor != null ? bedColor : "RED";
        this.icon = icon != null ? icon : "BED";
        this.iconVariant = iconVariant != null ? iconVariant : "DEFAULT";
    }

    public String getName() {
        return name;
    }

    public UUID getOwner() {
        return owner;
    }

    public Location getLocation() {
        World world = Bukkit.getWorld(worldName);
        if (world == null) {
            return null;
        }
        return new Location(world, x, y, z, yaw, pitch);
    }

    public String getWorldName() {
        return worldName;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
    
    public String getBedColor() {
        return bedColor;
    }
    
    public void setBedColor(String bedColor) {
        this.bedColor = bedColor;
    }
    
    public String getIcon() {
        return icon;
    }
    
    public void setIcon(String icon) {
        this.icon = icon;
    }
    
    public String getIconVariant() {
        return iconVariant;
    }
    
    public void setIconVariant(String iconVariant) {
        this.iconVariant = iconVariant;
    }
    
    public Material getIconMaterial() {
        switch (icon) {
            case "BED":
                return getBedMaterial();
            case "ANVIL":
                return getAnvilVariant();
            case "TOTEM":
                return Material.TOTEM_OF_UNDYING;
            case "STONE":
                return getStoneVariant();
            case "SWORD":
                return getSwordVariant();
            case "BELL":
                return Material.BELL;
            case "SAPLING":
                return getSaplingVariant();
            default:
                return Material.RED_BED;
        }
    }
    
    private Material getAnvilVariant() {
        switch (iconVariant) {
            case "DAMAGED":
                return Material.CHIPPED_ANVIL;
            case "VERY_DAMAGED":
                return Material.DAMAGED_ANVIL;
            default:
                return Material.ANVIL;
        }
    }
    
    private Material getStoneVariant() {
        switch (iconVariant) {
            // Overworld
            case "COBBLESTONE":
                return Material.COBBLESTONE;
            case "STONE_BRICKS":
                return Material.STONE_BRICKS;
            case "MOSSY_COBBLESTONE":
                return Material.MOSSY_COBBLESTONE;
            case "DEEPSLATE":
                return Material.DEEPSLATE;
            case "OBSIDIAN":
                return Material.OBSIDIAN;
            case "CRYING_OBSIDIAN":
                return Material.CRYING_OBSIDIAN;
            // Desert
            case "SAND":
                return Material.SAND;
            case "SANDSTONE":
                return Material.SANDSTONE;
            case "RED_SAND":
                return Material.RED_SAND;
            case "RED_SANDSTONE":
                return Material.RED_SANDSTONE;
            // Snow/Ice
            case "SNOW_BLOCK":
                return Material.SNOW_BLOCK;
            case "ICE":
                return Material.ICE;
            case "PACKED_ICE":
                return Material.PACKED_ICE;
            case "BLUE_ICE":
                return Material.BLUE_ICE;
            // Jungle/Forest
            case "MOSS_BLOCK":
                return Material.MOSS_BLOCK;
            case "MUDDY_MANGROVE_ROOTS":
                return Material.MUDDY_MANGROVE_ROOTS;
            // Ocean
            case "PRISMARINE":
                return Material.PRISMARINE;
            case "DARK_PRISMARINE":
                return Material.DARK_PRISMARINE;
            // Nether
            case "NETHERRACK":
                return Material.NETHERRACK;
            case "BLACKSTONE":
                return Material.BLACKSTONE;
            case "BASALT":
                return Material.BASALT;
            case "SOUL_SAND":
                return Material.SOUL_SAND;
            case "CRIMSON_NYLIUM":
                return Material.CRIMSON_NYLIUM;
            case "WARPED_NYLIUM":
                return Material.WARPED_NYLIUM;
            // End
            case "END_STONE":
                return Material.END_STONE;
            case "END_STONE_BRICKS":
                return Material.END_STONE_BRICKS;
            case "PURPUR_BLOCK":
                return Material.PURPUR_BLOCK;
            default:
                return Material.STONE;
        }
    }
    
    private Material getSwordVariant() {
        switch (iconVariant) {
            case "WOODEN":
                return Material.WOODEN_SWORD;
            case "STONE":
                return Material.STONE_SWORD;
            case "IRON":
                return Material.IRON_SWORD;
            case "GOLDEN":
                return Material.GOLDEN_SWORD;
            case "NETHERITE":
                return Material.NETHERITE_SWORD;
            default:
                return Material.DIAMOND_SWORD;
        }
    }
    
    private Material getSaplingVariant() {
        switch (iconVariant) {
            case "BIRCH":
                return Material.BIRCH_SAPLING;
            case "SPRUCE":
                return Material.SPRUCE_SAPLING;
            case "JUNGLE":
                return Material.JUNGLE_SAPLING;
            case "ACACIA":
                return Material.ACACIA_SAPLING;
            case "DARK_OAK":
                return Material.DARK_OAK_SAPLING;
            case "CHERRY":
                return Material.CHERRY_SAPLING;
            default:
                return Material.OAK_SAPLING;
        }
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
}

