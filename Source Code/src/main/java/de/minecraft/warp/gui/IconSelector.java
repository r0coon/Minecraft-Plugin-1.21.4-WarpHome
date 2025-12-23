package de.minecraft.warp.gui;

import de.minecraft.warp.WarpHome;
import de.minecraft.warp.model.Home;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
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

public class IconSelector implements InventoryHolder, Listener {
    
    private final WarpHome plugin;
    private final Player player;
    private final Home home;
    private Inventory inventory;
    
    public IconSelector(WarpHome plugin, Player player, Home home) {
        this.plugin = plugin;
        this.player = player;
        this.home = home;
        
        createInventory();
    }
    
    private void createInventory() {
        this.inventory = Bukkit.createInventory(this, 27, "§f§l✦ §8§lSelect Icon");
        setupInventory();
    }
    
    private void setupInventory() {
        inventory.clear();
        
        ItemStack grayPane = createPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        
        // Top row
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, grayPane);
        }
        
        // Middle rows - sides
        inventory.setItem(9, grayPane);
        inventory.setItem(17, grayPane);
        
        // Bottom row
        for (int i = 18; i < 27; i++) {
            if (i != 22) {
                inventory.setItem(i, grayPane);
            }
        }
        
        // Icons
        inventory.setItem(10, createIconItem(Material.RED_BED, "§c§lBed"));
        inventory.setItem(11, createIconItem(Material.ANVIL, "§7§lAnvil"));
        inventory.setItem(12, createIconItem(Material.TOTEM_OF_UNDYING, "§6§lTotem"));
        inventory.setItem(13, createIconItem(Material.STONE, "§8§lStone"));
        inventory.setItem(14, createIconItem(Material.DIAMOND_SWORD, "§b§lSword"));
        inventory.setItem(15, createIconItem(Material.BELL, "§e§lBell"));
        inventory.setItem(16, createIconItem(Material.OAK_SAPLING, "§2§lSapling"));
        
        // Close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§lClose");
        closeItem.setItemMeta(closeMeta);
        inventory.setItem(22, closeItem);
    }
    
    private ItemStack createIconItem(Material material, String displayName) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(displayName);
        item.setItemMeta(meta);
        return item;
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
        if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) return;
        
        int slot = event.getSlot();
        
        // Icon selection
        String selectedIcon = null;
        if (slot == 10) selectedIcon = "BED";
        else if (slot == 11) selectedIcon = "ANVIL";
        else if (slot == 12) selectedIcon = "TOTEM";
        else if (slot == 13) selectedIcon = "STONE";
        else if (slot == 14) selectedIcon = "SWORD";
        else if (slot == 15) selectedIcon = "BELL";
        else if (slot == 16) selectedIcon = "SAPLING";
        
        if (selectedIcon != null) {
            home.setIcon(selectedIcon);
            
            // Icons without variants (only TOTEM and BELL)
            if (selectedIcon.equals("TOTEM") || selectedIcon.equals("BELL")) {
                home.setIconVariant("DEFAULT");
                plugin.getHomeManager().saveHomes();
                
                clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                
                clickedPlayer.getWorld().spawnParticle(
                    Particle.VILLAGER_HAPPY,
                    clickedPlayer.getLocation().add(0, 2, 0),
                    15,
                    0.5, 0.5, 0.5,
                    0
                );
                
                clickedPlayer.closeInventory();
                
                // Reopen main GUI to show changes
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    MainGUI mainGUI = new MainGUI(plugin, clickedPlayer);
                    plugin.getServer().getPluginManager().registerEvents(mainGUI, plugin);
                    mainGUI.open();
                }, 2L);
                return;
            }
            // BED - open color selection
            else if (selectedIcon.equals("BED")) {
                plugin.getHomeManager().saveHomes();
                
                ColorSelector colorGUI = new ColorSelector(plugin, clickedPlayer, home);
                plugin.getServer().getPluginManager().registerEvents(colorGUI, plugin);
                colorGUI.open();
                return;
            }
            // Icons with variants
            else {
                if (home.getIconVariant() == null || home.getIconVariant().isEmpty()) {
                    home.setIconVariant("DEFAULT");
                }
                plugin.getHomeManager().saveHomes();
                
                VariantSelector variantGUI = new VariantSelector(plugin, clickedPlayer, home, selectedIcon);
                plugin.getServer().getPluginManager().registerEvents(variantGUI, plugin);
                variantGUI.open();
                return;
            }
        }
        
        // Close button
        if (slot == 22 || clickedItem.getType() == Material.BARRIER) {
            clickedPlayer.closeInventory();
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
        }
    }
}

