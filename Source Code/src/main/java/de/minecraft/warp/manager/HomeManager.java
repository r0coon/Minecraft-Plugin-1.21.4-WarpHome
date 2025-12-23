package de.minecraft.warp.manager;

import de.minecraft.warp.WarpHome;
import de.minecraft.warp.model.Home;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HomeManager {

    private final WarpHome plugin;
    private final File homesFile;
    private FileConfiguration homesConfig;
    private final Map<UUID, Map<String, Home>> homes;

    public HomeManager(WarpHome plugin) {
        this.plugin = plugin;
        this.homes = new HashMap<>();
        this.homesFile = new File(plugin.getDataFolder(), "homes.yml");
        
        loadHomes();
    }

    private void loadHomes() {
        if (!homesFile.exists()) {
            try {
                homesFile.getParentFile().mkdirs();
                homesFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create homes.yml!");
                e.printStackTrace();
                return;
            }
        }

        homesConfig = YamlConfiguration.loadConfiguration(homesFile);

        // Homes laden
        for (String uuidString : homesConfig.getKeys(false)) {
            UUID uuid = UUID.fromString(uuidString);
            ConfigurationSection playerSection = homesConfig.getConfigurationSection(uuidString);
            
            if (playerSection == null) continue;

            Map<String, Home> playerHomes = new HashMap<>();
            
            for (String homeName : playerSection.getKeys(false)) {
                ConfigurationSection homeSection = playerSection.getConfigurationSection(homeName);
                
                if (homeSection == null) continue;

                String world = homeSection.getString("world");
                double x = homeSection.getDouble("x");
                double y = homeSection.getDouble("y");
                double z = homeSection.getDouble("z");
                float yaw = (float) homeSection.getDouble("yaw");
                float pitch = (float) homeSection.getDouble("pitch");
                String bedColor = homeSection.getString("bedColor", "RED");
                String icon = homeSection.getString("icon", "BED");
                String iconVariant = homeSection.getString("iconVariant", "DEFAULT");

                Home home = new Home(homeName, uuid, world, x, y, z, yaw, pitch, bedColor, icon, iconVariant);
                playerHomes.put(homeName.toLowerCase(), home);
            }

            homes.put(uuid, playerHomes);
        }

        plugin.getLogger().info("Homes loaded successfully!");
    }

    public void saveHomes() {
        homesConfig = new YamlConfiguration();

        for (Map.Entry<UUID, Map<String, Home>> entry : homes.entrySet()) {
            String uuidString = entry.getKey().toString();
            
            for (Home home : entry.getValue().values()) {
                String path = uuidString + "." + home.getName();
                homesConfig.set(path + ".world", home.getWorldName());
                homesConfig.set(path + ".x", home.getX());
                homesConfig.set(path + ".y", home.getY());
                homesConfig.set(path + ".z", home.getZ());
                homesConfig.set(path + ".yaw", home.getYaw());
                homesConfig.set(path + ".pitch", home.getPitch());
                homesConfig.set(path + ".bedColor", home.getBedColor());
                homesConfig.set(path + ".icon", home.getIcon());
                homesConfig.set(path + ".iconVariant", home.getIconVariant());
            }
        }

        try {
            homesConfig.save(homesFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save homes.yml!");
            e.printStackTrace();
        }
    }

    public void setHome(UUID uuid, String name, Location location) {
        Map<String, Home> playerHomes = homes.computeIfAbsent(uuid, k -> new HashMap<>());
        Home home = new Home(name, uuid, location);
        playerHomes.put(name.toLowerCase(), home);
        saveHomes();
    }

    public Home getHome(UUID uuid, String name) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) {
            return null;
        }
        return playerHomes.get(name.toLowerCase());
    }

    public boolean deleteHome(UUID uuid, String name) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) {
            return false;
        }
        
        boolean removed = playerHomes.remove(name.toLowerCase()) != null;
        if (removed) {
            saveHomes();
        }
        return removed;
    }

    public List<Home> getHomes(UUID uuid) {
        Map<String, Home> playerHomes = homes.get(uuid);
        if (playerHomes == null) {
            return new ArrayList<>();
        }
        return new ArrayList<>(playerHomes.values());
    }

    public int getHomeCount(UUID uuid) {
        Map<String, Home> playerHomes = homes.get(uuid);
        return playerHomes == null ? 0 : playerHomes.size();
    }

    public int getMaxHomes() {
        return plugin.getConfig().getInt("max-homes", 0);
    }

    public boolean canSetMoreHomes(UUID uuid) {
        int max = getMaxHomes();
        if (max <= 0) {
            return true; 
        }
        return getHomeCount(uuid) < max;
    }
}

