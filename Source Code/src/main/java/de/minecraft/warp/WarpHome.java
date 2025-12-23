package de.minecraft.warp;

import de.minecraft.warp.commands.HomeCommand;
import de.minecraft.warp.gui.MainGUI;
import de.minecraft.warp.manager.HomeManager;
import de.minecraft.warp.manager.SettingsManager;
import de.minecraft.warp.manager.TeleportManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;

public class WarpHome extends JavaPlugin implements Listener {

    private HomeManager homeManager;
    private TeleportManager teleportManager;
    private SettingsManager settingsManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        // init managers i guess
        homeManager = new HomeManager(this);
        teleportManager = new TeleportManager(this);
        settingsManager = new SettingsManager(this);

        // regester command
        getCommand("home").setExecutor(new HomeCommand(this));
        
        // register chat thingy globaly
        getServer().getPluginManager().registerEvents(this, this);

        getLogger().info("HomePoint successfully loaded!");
    }
    
    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        
        if (!MainGUI.waitingForInput.containsKey(uuid)) return;
        
        // check if timeout (30 secs)
        Long startTime = MainGUI.inputTimeout.get(uuid);
        if (startTime != null && (System.currentTimeMillis() - startTime) > MainGUI.TIMEOUT_MS) {
            MainGUI.waitingForInput.remove(uuid);
            MainGUI.inputTimeout.remove(uuid);
            return;
        }
        
        event.setCancelled(true);
        MainGUI gui = MainGUI.waitingForInput.get(uuid);  // dont remove yet, just get it
        
        String homeName = event.getMessage().trim();
        
        // cancel stuff - remove from list
        if (homeName.equalsIgnoreCase("cancel")) {
            MainGUI.waitingForInput.remove(uuid);
            MainGUI.inputTimeout.remove(uuid);
            Bukkit.getScheduler().runTask(this, () -> {
                player.sendMessage("§c✗ §7Action cancelled.");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            });
            return;
        }
        
        // validaton stuff - keep waiting, show error msg
        if (homeName.length() < 2 || homeName.length() > 16) {
            // reset timeout thing
            MainGUI.inputTimeout.put(uuid, System.currentTimeMillis());
            Bukkit.getScheduler().runTask(this, () -> {
                player.sendMessage("§c⚠ §7Name must be §e2-16 §7characters! Try again:");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            });
            return;
        }
        
        // check chars allowed (a-z, 0-9, _, spaces)
        if (!homeName.matches("[a-zA-Z0-9_ ]+")) {
            // reset timeout again
            MainGUI.inputTimeout.put(uuid, System.currentTimeMillis());
            Bukkit.getScheduler().runTask(this, () -> {
                player.sendMessage("§c⚠ §7Only §ea-z, 0-9, _, spaces §7allowed! Try again:");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            });
            return;
        }
        
        // check max 3 spaces allowed
        long spaceCount = homeName.chars().filter(ch -> ch == ' ').count();
        if (spaceCount > 3) {
            // reset timeout once more
            MainGUI.inputTimeout.put(uuid, System.currentTimeMillis());
            Bukkit.getScheduler().runTask(this, () -> {
                player.sendMessage("§c⚠ §7Maximum §e3 spaces §7allowed! Try again:");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            });
            return;
        }
        
        // check if home alredy exists with this name
        if (homeManager.getHome(uuid, homeName) != null) {
            // reset timeout yep
            MainGUI.inputTimeout.put(uuid, System.currentTimeMillis());
            Bukkit.getScheduler().runTask(this, () -> {
                player.sendMessage("§c✗ §7Warp Point '§e" + homeName + "§7' already exists! Try again:");
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            });
            return;
        }
        
        // name is valid - remove from waiting and create home
        MainGUI.waitingForInput.remove(uuid);
        MainGUI.inputTimeout.remove(uuid);
        
        // set the home
        Bukkit.getScheduler().runTask(this, () -> {
            homeManager.setHome(uuid, homeName, player.getLocation());
            
            String msg = getMessage("home-set").replace("{name}", homeName);
            player.sendMessage(msg);
            
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.5f);

            player.getWorld().spawnParticle(
                Particle.VILLAGER_HAPPY,
                player.getLocation().add(0, 2, 0),
                20,
                0.5, 0.5, 0.5,
                0
            );

            player.getWorld().spawnParticle(
                Particle.END_ROD,
                player.getLocation().add(0, 1, 0),
                10,
                0.3, 0.5, 0.3,
                0.05
            );
        });
    }

    @Override
    public void onDisable() {
        if (teleportManager != null) {
            teleportManager.cancelAll();
        }

        getLogger().info("HomePoint disabled!");
    }

    public HomeManager getHomeManager() {
        return homeManager;
    }

    public TeleportManager getTeleportManager() {
        return teleportManager;
    }

    public SettingsManager getSettingsManager() {
        return settingsManager;
    }

    public String getMessage(String key) {
        return getConfig().getString("messages." + key, "&cNachricht nicht gefunden: " + key)
                .replace("&", "§");
    }
}

