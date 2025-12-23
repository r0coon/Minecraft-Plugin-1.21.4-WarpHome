package de.minecraft.warp.gui;

import de.minecraft.warp.WarpHome;
import de.minecraft.warp.manager.TeleportManager;
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
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainGUI implements InventoryHolder, Listener {

    private final WarpHome plugin;
    private final Player player;
    private Inventory inventory;
    private List<Home> homes;
    public static final Map<UUID, MainGUI> waitingForInput = new HashMap<>();
    public static final Map<UUID, Long> inputTimeout = new HashMap<>();
    public static final long TIMEOUT_MS = 30000; // 30 seconds

    public MainGUI(WarpHome plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        this.homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        
        createInventory();
    }

    private void createInventory() {
        int size = 54; // 6 rows (maximum)
        this.inventory = Bukkit.createInventory(this, size, "§f§l⌂ §8§lWarp Collection");
        
        setupInventory();
    }

    private void setupInventory() {
        inventory.clear();
        
        createFancyBorder();
        
        // Add homes as items (35 slots available)
        int homeIndex = 0;
        int[] slots = {10, 11, 12, 13, 14, 15, 16,   // Row 2
                       19, 20, 21, 22, 23, 24, 25,   // Row 3
                       28, 29, 30, 31, 32, 33, 34,   // Row 4
                       37, 38, 39, 40, 41, 42, 43,   // Row 5
                       46, 48, 49, 50, 52};          // Row 6
        
        for (int slot : slots) {
            if (homeIndex < homes.size()) {
                Home home = homes.get(homeIndex);
                ItemStack item = createHomeItem(home);
                inventory.setItem(slot, item);
                homeIndex++;
            }
        }

        // Info button (Slot 4)
        ItemStack infoItem = new ItemStack(Material.BOOK);
        ItemMeta infoMeta = infoItem.getItemMeta();
        infoMeta.setDisplayName("§8§l━━━ §e§lInfo §8§l━━━");
        List<String> infoLore = new ArrayList<>();
        infoLore.add("");
        infoLore.add("§7Saved warp points: §b" + homes.size());
        int maxHomes = plugin.getHomeManager().getMaxHomes();
        if (maxHomes > 0) {
            infoLore.add("§7Maximum: §b" + maxHomes);
        } else {
            infoLore.add("§7Maximum: §b∞");
        }
        infoLore.add("");
        infoLore.add("§a§lLeft click §8→ §7Teleport");
        infoLore.add("§e§lShift + Left click §8→ §7Icon");
        infoLore.add("§c§lShift + Right click §8→ §7Delete");
        infoLore.add("");
        infoMeta.setLore(infoLore);
        infoItem.setItemMeta(infoMeta);
        inventory.setItem(4, infoItem);

        // New Warp Point button (Slot 47)
        ItemStack addHome = new ItemStack(Material.LODESTONE);
        ItemMeta addMeta = addHome.getItemMeta();
        addMeta.setDisplayName("§a§l+ New Warp Point");
        addMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        addMeta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_POTION_EFFECTS);
        addHome.setItemMeta(addMeta);
        inventory.setItem(47, addHome);

        // Close button (Slot 51)
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        closeMeta.setDisplayName("§c§l✖ Close");
        closeItem.setItemMeta(closeMeta);
        inventory.setItem(51, closeItem);
    }

    private void createFancyBorder() {
        ItemStack grayPane = createPane(Material.GRAY_STAINED_GLASS_PANE, " ");
        
        // Top row - Skip 4 for Info button
        for (int i = 0; i < 9; i++) {
            if (i != 4) {
                inventory.setItem(i, grayPane);
            }
        }
        
        // Middle rows - left and right borders
        for (int row = 1; row < 5; row++) {
            inventory.setItem(row * 9, grayPane);
            inventory.setItem(row * 9 + 8, grayPane);
        }
        
        // Bottom row - All gray except 47 (New Home) and 51 (Close)
        for (int i = 45; i < 54; i++) {
            if (i != 47 && i != 51) {
                inventory.setItem(i, grayPane);
            }
        }
    }

    private ItemStack createPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        meta.setDisplayName(name);
        pane.setItemMeta(meta);
        return pane;
    }

    private ItemStack createHomeItem(Home home) {
        ItemStack item = new ItemStack(home.getIconMaterial());
        ItemMeta meta = item.getItemMeta();
        
        meta.setDisplayName("§8§l━━━ §b§l" + home.getName() + " §8§l━━━");
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
        
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add("§8§m━━━━━━━━━━━━━━━━━━━━━");
        lore.add("§7Coordinates:");
        lore.add("  §eX: §f" + (int) home.getX());
        lore.add("  §eY: §f" + (int) home.getY());
        lore.add("  §eZ: §f" + (int) home.getZ());
        lore.add("§8§m━━━━━━━━━━━━━━━━━━━━━");
        lore.add("");
        lore.add("§e§lControls:");
        lore.add("");
        lore.add("§a§l> §aLeft click §8→ §7Teleport");
        lore.add("§e§l> §eShift + Left click §8→ §7Icon");
        lore.add("§c§l> §cShift + Right click §8→ §7Delete");
        lore.add("");
        
        meta.setLore(lore);
        item.setItemMeta(meta);
        
        return item;
    }

    public void open() {
        player.openInventory(inventory);
        
        player.getWorld().spawnParticle(
            Particle.PORTAL,
            player.getLocation().add(0, 2, 0),
            30,
            0.5, 0.5, 0.5,
            0.5
        );
    }

    public void refresh() {
        this.homes = plugin.getHomeManager().getHomes(player.getUniqueId());
        setupInventory();
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;

        event.setCancelled(true);

        Player clickedPlayer = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;
        
        // Info button - not clickable
        if (clickedItem.getType() == Material.BOOK) {
            return;
        }
        
        // Close button
        if (clickedItem.getType() == Material.BARRIER) {
            clickedPlayer.closeInventory();
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
            return;
        }

        // New Home button
        if (clickedItem.getType() == Material.LODESTONE) {
            if (!plugin.getHomeManager().canSetMoreHomes(clickedPlayer.getUniqueId())) {
                String msg = plugin.getMessage("max-homes-reached")
                        .replace("{max}", String.valueOf(plugin.getHomeManager().getMaxHomes()));
                clickedPlayer.sendMessage(msg);
                clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            clickedPlayer.closeInventory();
            
            // Wait a tick before setting up chat listener to avoid race condition
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                clickedPlayer.sendMessage("");
                clickedPlayer.sendMessage("§b✎ §7Enter a name for your new warp point:");
                clickedPlayer.sendMessage("§8▸ §7Type §c'cancel' §7to abort");
                clickedPlayer.sendMessage("");
                clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);
                
                waitingForInput.put(clickedPlayer.getUniqueId(), this);
                inputTimeout.put(clickedPlayer.getUniqueId(), System.currentTimeMillis());
            }, 1L);
            return;
        }

        // Ignore glass panes
        if (clickedItem.getType() == Material.GRAY_STAINED_GLASS_PANE) return;

        // Home item clicked
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        String displayName = meta.getDisplayName();
        // Remove all formatting: "§8§l━━━ §b§lMyHome §8§l━━━" -> "MyHome"
        String homeName = displayName
            .replace("§8§l━━━ §b§l", "")
            .replace(" §8§l━━━", "")
            .trim();

        Home selectedHome = null;
        for (Home home : homes) {
            if (home.getName().equalsIgnoreCase(homeName)) {
                selectedHome = home;
                break;
            }
        }

        if (selectedHome == null) return;

        // Shift + Left click for Settings
        if (event.isShiftClick() && event.isLeftClick()) {
            clickedPlayer.closeInventory();
            
            IconSelector iconGUI = new IconSelector(plugin, clickedPlayer, selectedHome);
            plugin.getServer().getPluginManager().registerEvents(iconGUI, plugin);
            iconGUI.open();
            return;
        }

        // Shift + Right click to Delete
        if (event.isShiftClick() && event.isRightClick()) {
            clickedPlayer.closeInventory();
            
            DeleteConfirmation confirmGUI = new DeleteConfirmation(plugin, clickedPlayer, selectedHome);
            plugin.getServer().getPluginManager().registerEvents(confirmGUI, plugin);
            confirmGUI.open();
            return;
        }

        // Left click to Teleport
        if (event.isLeftClick()) {
            clickedPlayer.closeInventory();
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 1.5f);
            
            if (selectedHome.getLocation() == null) {
                clickedPlayer.sendMessage("§c⚠ §7World no longer exists!");
                return;
            }

            TeleportManager teleportManager = plugin.getTeleportManager();
            if (teleportManager.isTeleporting(clickedPlayer.getUniqueId())) {
                clickedPlayer.sendMessage("§c⚠ §7Already teleporting!");
                return;
            }

            teleportManager.startTeleport(clickedPlayer, selectedHome.getLocation(), selectedHome.getName());
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory().equals(inventory)) {
            // Only unregister if we're not waiting for chat input
            if (!waitingForInput.containsKey(player.getUniqueId())) {
                HandlerList.unregisterAll(this);
            }
        }
    }

    // Chat handler moved to WarpHome.java for global registration
}

