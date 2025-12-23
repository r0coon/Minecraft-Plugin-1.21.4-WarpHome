package de.minecraft.warp.gui;

import de.minecraft.warp.WarpHome;
import de.minecraft.warp.model.Home;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ColorSelector implements InventoryHolder, Listener {
    
    private final WarpHome plugin;
    private final Player player;
    private final Home home;
    private Inventory inventory;
    
    private static final String[][] COLORS = {
        {"WHITE", "ORANGE", "MAGENTA", "LIGHT_BLUE"},
        {"YELLOW", "LIME", "PINK", "GRAY"},
        {"LIGHT_GRAY", "CYAN", "PURPLE", "BLUE"},
        {"BROWN", "GREEN", "RED", "BLACK"}
    };
    
    public ColorSelector(WarpHome plugin, Player player, Home home) {
        this.plugin = plugin;
        this.player = player;
        this.home = home;
        
        createInventory();
    }
    
    private void createInventory() {
        this.inventory = Bukkit.createInventory(this, 54, "§f§l✦ §8§lSelect Bed Color");
        setupInventory();
    }
    
    private void setupInventory() {
        inventory.clear();
        
        // Gray border
        ItemStack grayPane = createPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        
        // Top row (0-8)
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, grayPane);
        }
        
        // Middle rows - sides only (left and right borders)
        for (int row = 1; row < 5; row++) {
            inventory.setItem(row * 9, grayPane);      // Left border
            inventory.setItem(row * 9 + 8, grayPane);  // Right border
        }
        
        // Bottom row (45-53), except close button at 49
        for (int i = 45; i < 54; i++) {
            if (i != 49) {
                inventory.setItem(i, grayPane);
            }
        }
        
        // Add bed colors in a centered, symmetrical 4x4 grid
        // Row 1: Slots 10-13 (centered, 1 slot padding on each side)
        // Row 2: Slots 19-22 (centered)
        // Row 3: Slots 28-31 (centered)
        // Row 4: Slots 37-40 (centered)
        int[] bedSlots = {
            10, 11, 12, 13,  // Row 1: 4 beds
            19, 20, 21, 22,  // Row 2: 4 beds
            28, 29, 30, 31,  // Row 3: 4 beds
            37, 38, 39, 40   // Row 4: 4 beds
        };
        
        int colorIndex = 0;
        for (String[] row : COLORS) {
            for (String color : row) {
                ItemStack bed = createBedItem(color);
                inventory.setItem(bedSlots[colorIndex], bed);
                colorIndex++;
            }
        }
        
        // Fill empty slots around beds with glass panes
        // Row 1: Slots 14-17 (right side)
        for (int i = 14; i < 18; i++) {
            inventory.setItem(i, grayPane);
        }
        // Row 2: Slots 23-26 (right side)
        for (int i = 23; i < 27; i++) {
            inventory.setItem(i, grayPane);
        }
        // Row 3: Slots 32-35 (right side)
        for (int i = 32; i < 36; i++) {
            inventory.setItem(i, grayPane);
        }
        // Row 4: Slots 41-44 (right side)
        for (int i = 41; i < 45; i++) {
            inventory.setItem(i, grayPane);
        }
        
        // Close button (center bottom)
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§lClose");
        List<String> closeLore = new ArrayList<>();
        closeLore.add("");
        closeLore.add("§7Close this menu");
        closeLore.add("");
        closeMeta.setLore(closeLore);
        closeItem.setItemMeta(closeMeta);
        inventory.setItem(49, closeItem);
    }
    
    private ItemStack createBedItem(String color) {
        Material bedMaterial = getBedMaterial(color);
        ItemStack bed = new ItemStack(bedMaterial);
        ItemMeta meta = bed.getItemMeta();
        
        String displayName = color.replace("_", " ");
        displayName = "§e§l" + displayName.substring(0, 1).toUpperCase() + displayName.substring(1).toLowerCase() + " Bed";
        
        meta.setDisplayName(displayName);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        if (home.getBedColor().equals(color)) {
            lore.add("§a§l✓ Currently selected");
        } else {
            lore.add("§7Click to select");
        }
        lore.add("");
        meta.setLore(lore);
        
        bed.setItemMeta(meta);
        return bed;
    }
    
    private Material getBedMaterial(String color) {
        return switch (color) {
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
    
    private ItemStack createPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }
    
    public void open() {
        player.openInventory(inventory);
    }
    
    @Override
    public Inventory getInventory() {
        return inventory;
    }
    
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            HandlerList.unregisterAll(this);
        }
    }
    
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        
        event.setCancelled(true);
        
        Player clickedPlayer = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        // Ignore glass panes
        String itemType = clickedItem.getType().toString();
        if (itemType.endsWith("_STAINED_GLASS_PANE")) {
            return;
        }
        
        // Close button
        if (clickedItem.getType() == Material.BARRIER) {
            clickedPlayer.closeInventory();
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
            return;
        }
        
        // Bed selected
        if (clickedItem.getType().toString().endsWith("_BED")) {
            String colorName = clickedItem.getType().toString().replace("_BED", "");
            
            home.setBedColor(colorName);
            plugin.getHomeManager().saveHomes();
            
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
            clickedPlayer.closeInventory();
            
            // Reopen main GUI to show changes
            org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                MainGUI mainGUI = new MainGUI(plugin, clickedPlayer);
                plugin.getServer().getPluginManager().registerEvents(mainGUI, plugin);
                mainGUI.open();
            }, 2L);
        }
    }
}

