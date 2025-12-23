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

import java.util.Arrays;
import java.util.List;

public class VariantSelector implements InventoryHolder, Listener {
    private final WarpHome plugin;
    private final Player player;
    private final Home home;
    private final String iconType;
    private Inventory inventory;

    public VariantSelector(WarpHome plugin, Player player, Home home, String iconType) {
        this.plugin = plugin;
        this.player = player;
        this.home = home;
        this.iconType = iconType;
    }

    public void open() {
        this.inventory = Bukkit.createInventory(this, 54, "§f§l✦ §8§lSelect Variant");

        ItemStack grayPane = createPane(Material.GRAY_STAINED_GLASS_PANE, " ");

        // Top border
        for (int i = 0; i < 9; i++) {
            inventory.setItem(i, grayPane);
        }

        // Middle sides
        for (int row = 1; row < 5; row++) {
            inventory.setItem(row * 9, grayPane);
            inventory.setItem(row * 9 + 8, grayPane);
        }

        // Bottom border
        for (int i = 45; i < 54; i++) {
            if (i != 49) {
                inventory.setItem(i, grayPane);
            }
        }

        // Add variants in a structured grid (5 rows x 7 columns = 35 slots max)
        List<VariantOption> variants = getVariantsForIcon(iconType);
        
        // Define slots for a centered 7-column grid
        int[] variantSlots = {
            10, 11, 12, 13, 14, 15, 16,  // Row 1
            19, 20, 21, 22, 23, 24, 25,  // Row 2
            28, 29, 30, 31, 32, 33, 34,  // Row 3
            37, 38, 39, 40, 41, 42, 43   // Row 4 (if needed)
        };
        
        for (int i = 0; i < variants.size() && i < variantSlots.length; i++) {
            VariantOption variant = variants.get(i);
            ItemStack item = new ItemStack(variant.material);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName("§e§l" + variant.name);
                meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ATTRIBUTES);
                item.setItemMeta(meta);
            }
            inventory.setItem(variantSlots[i], item);
        }

        // Close button
        ItemStack closeItem = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName("§c§lClose");
            closeItem.setItemMeta(closeMeta);
        }
        inventory.setItem(49, closeItem);

        player.openInventory(inventory);
    }

    private List<VariantOption> getVariantsForIcon(String iconType) {
        switch (iconType) {
            case "ANVIL":
                return Arrays.asList(
                    new VariantOption("DEFAULT", "Anvil", Material.ANVIL),
                    new VariantOption("DAMAGED", "Chipped Anvil", Material.CHIPPED_ANVIL),
                    new VariantOption("VERY_DAMAGED", "Damaged Anvil", Material.DAMAGED_ANVIL)
                );
            case "STONE":
                return Arrays.asList(
                    // Overworld
                    new VariantOption("DEFAULT", "Stone", Material.STONE),
                    new VariantOption("COBBLESTONE", "Cobblestone", Material.COBBLESTONE),
                    new VariantOption("STONE_BRICKS", "Stone Bricks", Material.STONE_BRICKS),
                    new VariantOption("MOSSY_COBBLESTONE", "Mossy Cobblestone", Material.MOSSY_COBBLESTONE),
                    new VariantOption("DEEPSLATE", "Deepslate", Material.DEEPSLATE),
                    new VariantOption("OBSIDIAN", "Obsidian", Material.OBSIDIAN),
                    new VariantOption("CRYING_OBSIDIAN", "Crying Obsidian", Material.CRYING_OBSIDIAN),
                    // Desert
                    new VariantOption("SAND", "Sand", Material.SAND),
                    new VariantOption("SANDSTONE", "Sandstone", Material.SANDSTONE),
                    new VariantOption("RED_SAND", "Red Sand", Material.RED_SAND),
                    new VariantOption("RED_SANDSTONE", "Red Sandstone", Material.RED_SANDSTONE),
                    // Snow/Ice
                    new VariantOption("SNOW_BLOCK", "Snow Block", Material.SNOW_BLOCK),
                    new VariantOption("ICE", "Ice", Material.ICE),
                    new VariantOption("PACKED_ICE", "Packed Ice", Material.PACKED_ICE),
                    new VariantOption("BLUE_ICE", "Blue Ice", Material.BLUE_ICE),
                    // Jungle/Forest
                    new VariantOption("MOSS_BLOCK", "Moss Block", Material.MOSS_BLOCK),
                    new VariantOption("MUDDY_MANGROVE_ROOTS", "Mangrove Roots", Material.MUDDY_MANGROVE_ROOTS),
                    // Ocean
                    new VariantOption("PRISMARINE", "Prismarine", Material.PRISMARINE),
                    new VariantOption("DARK_PRISMARINE", "Dark Prismarine", Material.DARK_PRISMARINE),
                    // Nether
                    new VariantOption("NETHERRACK", "Netherrack", Material.NETHERRACK),
                    new VariantOption("BLACKSTONE", "Blackstone", Material.BLACKSTONE),
                    new VariantOption("BASALT", "Basalt", Material.BASALT),
                    new VariantOption("SOUL_SAND", "Soul Sand", Material.SOUL_SAND),
                    new VariantOption("CRIMSON_NYLIUM", "Crimson Nylium", Material.CRIMSON_NYLIUM),
                    new VariantOption("WARPED_NYLIUM", "Warped Nylium", Material.WARPED_NYLIUM),
                    // End
                    new VariantOption("END_STONE", "End Stone", Material.END_STONE),
                    new VariantOption("END_STONE_BRICKS", "End Stone Bricks", Material.END_STONE_BRICKS),
                    new VariantOption("PURPUR_BLOCK", "Purpur Block", Material.PURPUR_BLOCK)
                );
            case "SWORD":
                return Arrays.asList(
                    new VariantOption("WOODEN", "Wooden Sword", Material.WOODEN_SWORD),
                    new VariantOption("STONE", "Stone Sword", Material.STONE_SWORD),
                    new VariantOption("IRON", "Iron Sword", Material.IRON_SWORD),
                    new VariantOption("DEFAULT", "Diamond Sword", Material.DIAMOND_SWORD),
                    new VariantOption("GOLDEN", "Golden Sword", Material.GOLDEN_SWORD),
                    new VariantOption("NETHERITE", "Netherite Sword", Material.NETHERITE_SWORD)
                );
            case "SAPLING":
                return Arrays.asList(
                    new VariantOption("DEFAULT", "Oak Sapling", Material.OAK_SAPLING),
                    new VariantOption("BIRCH", "Birch Sapling", Material.BIRCH_SAPLING),
                    new VariantOption("SPRUCE", "Spruce Sapling", Material.SPRUCE_SAPLING),
                    new VariantOption("JUNGLE", "Jungle Sapling", Material.JUNGLE_SAPLING),
                    new VariantOption("ACACIA", "Acacia Sapling", Material.ACACIA_SAPLING),
                    new VariantOption("DARK_OAK", "Dark Oak Sapling", Material.DARK_OAK_SAPLING),
                    new VariantOption("CHERRY", "Cherry Sapling", Material.CHERRY_SAPLING)
                );
            default:
                return Arrays.asList(new VariantOption("DEFAULT", "Default", Material.STONE));
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        if (!event.getInventory().equals(inventory)) return;
        
        event.setCancelled(true);
        
        Player clickedPlayer = (Player) event.getWhoClicked();
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || clicked.getType() == Material.AIR) return;

        if (clicked.getType() == Material.BARRIER) {
            clickedPlayer.closeInventory();
            clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.UI_BUTTON_CLICK, 1.0f, 0.8f);
            return;
        }

        // Variant selection
        if (clicked.getType() != Material.GRAY_STAINED_GLASS_PANE) {
            List<VariantOption> variants = getVariantsForIcon(iconType);
            for (VariantOption variant : variants) {
                if (clicked.getType() == variant.material) {
                    home.setIconVariant(variant.key);
                    plugin.getHomeManager().saveHomes();
                    clickedPlayer.playSound(clickedPlayer.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.5f);
                    
                    clickedPlayer.closeInventory();
                    
                    // Reopen main GUI to show changes
                    org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        MainGUI mainGUI = new MainGUI(plugin, clickedPlayer);
                        plugin.getServer().getPluginManager().registerEvents(mainGUI, plugin);
                        mainGUI.open();
                    }, 2L);
                    return;
                }
            }
        }
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

    private ItemStack createPane(Material material, String name) {
        ItemStack pane = new ItemStack(material);
        ItemMeta meta = pane.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            pane.setItemMeta(meta);
        }
        return pane;
    }

    private static class VariantOption {
        String key;
        String name;
        Material material;

        VariantOption(String key, String name, Material material) {
            this.key = key;
            this.name = name;
            this.material = material;
        }
    }
}

