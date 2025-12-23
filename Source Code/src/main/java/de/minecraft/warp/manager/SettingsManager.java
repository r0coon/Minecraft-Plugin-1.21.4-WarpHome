package de.minecraft.warp.manager;

import de.minecraft.warp.WarpHome;
import de.minecraft.warp.model.PlayerSettings;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SettingsManager {
    
    private final WarpHome plugin;
    private final File settingsFile;
    private FileConfiguration settingsConfig;
    private final Map<UUID, PlayerSettings> settings;
    
    public SettingsManager(WarpHome plugin) {
        this.plugin = plugin;
        this.settings = new HashMap<>();
        this.settingsFile = new File(plugin.getDataFolder(), "settings.yml");
        
        loadSettings();
    }
    
    private void loadSettings() {
        // dont create settings file anymore, just use defaults
        // if file exists, load it, otherwise just use default settings
        if (settingsFile.exists()) {
            settingsConfig = YamlConfiguration.loadConfiguration(settingsFile);
            
            for (String uuidString : settingsConfig.getKeys(false)) {
                UUID uuid = UUID.fromString(uuidString);
                ConfigurationSection section = settingsConfig.getConfigurationSection(uuidString);
                
                if (section == null) continue;
                
                PlayerSettings playerSettings = new PlayerSettings();
                playerSettings.setBedColor(section.getString("bed-color", "RED"));
                playerSettings.setParticleType(section.getString("particle-type", "STANDARD"));
                playerSettings.setParticleColor(section.getString("particle-color", "BLUE"));
                playerSettings.setSoundEnabled(section.getBoolean("sound-enabled", true));
                
                settings.put(uuid, playerSettings);
            }
        }
        // if file doesnt exist, just use default settings (no file creation)
    }
    
    public void saveSettings() {
        // dont save settings file anymore
        // settings are just in memory now
    }
    
    public PlayerSettings getSettings(UUID uuid) {
        return settings.computeIfAbsent(uuid, k -> new PlayerSettings());
    }
    
    public void setSettings(UUID uuid, PlayerSettings settings) {
        this.settings.put(uuid, settings);
        // dont save to file anymore
    }
}

