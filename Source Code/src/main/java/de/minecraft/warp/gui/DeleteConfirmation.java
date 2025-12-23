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

import java.util.ArrayList;
import java.util.List;

public class DeleteConfirmation implements InventoryHolder, Listener {
    
    private final WarpHome plugin;
    private final Player player;
    private final Home home;
    private Inventory inventory;
    
    public DeleteConfirmation(WarpHome plugin, Player player, Home home) {
        this.plugin = plugin;
        this.player = player;
        this.home = home;
        
        createInventory();
    }
    
    private void createInventory() {
        this.inventory = Bukkit.createInventory(this, 27, "§f§l⚠ §c§lConfirm Delete");
        setupInventory();
    }
    
    private void setupInventory() {
        inventory.clear();
        
        ItemStack grayPane = createPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        
        // Top row (except slot 4)
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                inventory.setItem(i, grayPane);
            }
        }
        
        // Middle row - sides
        inventory.setItem(9, grayPane);
        inventory.setItem(17, grayPane);
        
        // Bottom row
        for (int i = 18; i < 27; i++) {
            inventory.setItem(i, grayPane);
        }
        
        // Home info at top center
        ItemStack homeInfo = new ItemStack(home.getIconMaterial());
        ItemMeta homeInfoMeta = homeInfo.getItemMeta();
        homeInfoMeta.setDisplayName("§8§l━━━ §e§l" + home.getName() + " §8§l━━━");
        List<String> homeInfoLore = new ArrayList<>();
        homeInfoLore.add("");
        homeInfoLore.add("§8§m━━━━━━━━━━━━━━━━━━━━━");
        homeInfoLore.add("§7Coordinates:");
        homeInfoLore.add("  §eX: §f" + (int) home.getX());
        homeInfoLore.add("  §eY: §f" + (int) home.getY());
        homeInfoLore.add("  §eZ: §f" + (int) home.getZ());
        homeInfoLore.add("§8§m━━━━━━━━━━━━━━━━━━━━━");
        homeInfoLore.add("");
        homeInfoLore.add("§c§lThis home will be deleted!");
        homeInfoLore.add("");
        homeInfoMeta.setLore(homeInfoLore);
        homeInfo.setItemMeta(homeInfoMeta);
        inventory.setItem(4, homeInfo);
        
        // Confirm (Lime)
        ItemStack confirmItem = new ItemStack(Material.LIME_STAINED_GLASS_PANE);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName("§a§l✓ Yes, delete");
        confirmItem.setItemMeta(confirmMeta);
        inventory.setItem(11, confirmItem);
        
        // Cancel (Red)
        ItemStack cancelItem = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        cancelMeta.setDisplayName("§c§l✖ No, cancel");
        cancelItem.setItemMeta(cancelMeta);
        inventory.setItem(15, cancelItem);
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
        
        // Confirm - Delete
        if (clickedItem.getType() == Material.LIME_STAINED_GLASS_PANE) {
            plugin.getHomeManager().deleteHome(clickedPlayer.getUniqueId(), home.getName());
            
            String msg = plugin.getMessage("home-deleted").replace("{name}", home.getName());
            clickedPlayer.sendMessage(msg);
            
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 0.8f);
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.BLOCK_GLASS_BREAK, 1.0f, 0.5f);
            
            clickedPlayer.getWorld().spawnParticle(
                Particle.SMOKE_LARGE,
                clickedPlayer.getLocation().add(0, 1, 0),
                15,
                0.3, 0.5, 0.3,
                0.01
            );
            
            clickedPlayer.getWorld().spawnParticle(
                Particle.LAVA,
                clickedPlayer.getLocation().add(0, 1, 0),
                5,
                0.3, 0.3, 0.3,
                0
            );
            
            clickedPlayer.closeInventory();
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                MainGUI homeGUI = new MainGUI(plugin, clickedPlayer);
                plugin.getServer().getPluginManager().registerEvents(homeGUI, plugin);
                homeGUI.open();
            }, 10L);
            
            return;
        }
        
        // Cancel
        if (clickedItem.getType() == Material.RED_STAINED_GLASS_PANE) {
            clickedPlayer.closeInventory();
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
            // No chat message - just close and reopen
            
            MainGUI homeGUI = new MainGUI(plugin, clickedPlayer);
            plugin.getServer().getPluginManager().registerEvents(homeGUI, plugin);
            homeGUI.open();
        }
    }
}

