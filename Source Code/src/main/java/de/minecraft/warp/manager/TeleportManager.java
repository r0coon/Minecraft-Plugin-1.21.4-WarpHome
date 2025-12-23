package de.minecraft.warp.manager;

import de.minecraft.warp.WarpHome;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TeleportManager {

    private final WarpHome plugin;
    private final Map<UUID, TeleportTask> activeTeleports;

    public TeleportManager(WarpHome plugin) {
        this.plugin = plugin;
        this.activeTeleports = new HashMap<>();
    }

    public void startTeleport(Player player, Location destination, String homeName) {
        // check if already teleporting
        if (activeTeleports.containsKey(player.getUniqueId())) {
            player.sendMessage(plugin.getMessage("teleport-cancelled"));
            return;
        }

        int delay = plugin.getConfig().getInt("teleport-delay", 5);
        boolean cancelOnMove = plugin.getConfig().getBoolean("cancel-on-move", true);

        TeleportTask task = new TeleportTask(player, destination, homeName, delay, cancelOnMove);
        activeTeleports.put(player.getUniqueId(), task);
        task.start();
    }

    public void cancelTeleport(UUID uuid) {
        TeleportTask task = activeTeleports.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    public void cancelAll() {
        for (TeleportTask task : activeTeleports.values()) {
            task.cancel();
        }
        activeTeleports.clear();
    }

    public boolean isTeleporting(UUID uuid) {
        return activeTeleports.containsKey(uuid);
    }

    private class TeleportTask {
        private final Player player;
        private final Location destination;
        private final String homeName;
        private final int totalSeconds;
        private final boolean cancelOnMove;
        private final Location startLocation;
        private BukkitTask task;
        private int secondsLeft;

        public TeleportTask(Player player, Location destination, String homeName, int seconds, boolean cancelOnMove) {
            this.player = player;
            this.destination = destination;
            this.homeName = homeName;
            this.totalSeconds = seconds;
            this.cancelOnMove = cancelOnMove;
            this.startLocation = player.getLocation().clone();
            this.secondsLeft = seconds;
        }

        public void start() {
            // load player settings
            boolean soundEnabled = plugin.getSettingsManager().getSettings(player.getUniqueId()).isSoundEnabled();
            
            // start sound
            if (soundEnabled) {
                player.playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1.0f, 1.5f);
            }

            task = new BukkitRunnable() {
                private int particleAngle = 0;

                @Override
                public void run() {
                    // check if player still online
                    if (!player.isOnline()) {
                        cancel();
                        activeTeleports.remove(player.getUniqueId());
                        return;
                    }

                    // check if player moved
                    if (cancelOnMove && hasMoved()) {
                        player.sendMessage("§c⚠ §7Teleportation cancelled!");
                        cancel();
                        activeTeleports.remove(player.getUniqueId());
                        return;
                    }

                    if (secondsLeft > 0) {
                        // update actionbar
                        String actionbar = "§bTeleporting in " + secondsLeft + "s";
                        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(actionbar));

                        // spawn particles circle with player settings
                        spawnCircleParticles();

                        // sound every tick (only if enabled)
                        boolean soundEnabled = plugin.getSettingsManager().getSettings(player.getUniqueId()).isSoundEnabled();
                        if (soundEnabled) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.5f, 1.0f + (float) secondsLeft / 10);
                        }

                        secondsLeft--;
                    } else {
                        // do the teleport
                        completeTeleport();
                        cancel();
                        activeTeleports.remove(player.getUniqueId());
                    }
                }

                private void spawnCircleParticles() {
                    Location loc = player.getLocation();
                    double baseRadius = 1.5;
                    int particles = 15;  // reduced from 30 to 15
                    
                    org.bukkit.Color particleColor = plugin.getSettingsManager().getSettings(player.getUniqueId()).getParticleColor();
                    Particle particleType = plugin.getSettingsManager().getSettings(player.getUniqueId()).getParticle();

                    // pulsing effect
                    double pulseRadius = baseRadius + Math.sin(particleAngle * 0.1) * 0.3;
                    
                    // triple rotating circle thing
                    for (int ring = 0; ring < 3; ring++) {
                        double ringRadius = pulseRadius - (ring * 0.4);
                        double ringHeight = 0.3 + (ring * 0.5);
                        
                        for (int i = 0; i < particles; i++) {
                            double angle = (2 * Math.PI * i / particles) + (particleAngle * Math.PI / 180.0) + (ring * Math.PI / 3);
                            double x = loc.getX() + ringRadius * Math.cos(angle);
                            double z = loc.getZ() + ringRadius * Math.sin(angle);
                            double y = loc.getY() + ringHeight;

                            // Mystische farbige Partikel
                            player.getWorld().spawnParticle(
                                    Particle.REDSTONE,
                                    x, y, z,
                                    1,
                                    0, 0, 0,
                                    0,
                                    new Particle.DustOptions(particleColor, 2.0f)
                            );

                            if (i % 3 == 0) {
                                // Weiße Partikel für mystische Atmosphäre
                                player.getWorld().spawnParticle(
                                        Particle.FIREWORKS_SPARK,
                                        x, y + 0.5, z,
                                        1,
                                        0.05, 0.1, 0.05,
                                        0.02
                                );
                            }
                        }
                    }

                    // Aufsteigende Spirale mit mehreren Strängen
                    double spiralProgress = 1.0 - ((double) secondsLeft / totalSeconds);
                    for (int strand = 0; strand < 3; strand++) {  // Reduziert von 5 auf 3 Stränge
                        for (int i = 0; i < 5; i++) {  // Reduziert von 8 auf 5
                            double height = spiralProgress * 3.0;
                            double angle = (2 * Math.PI * strand / 5) + (particleAngle * Math.PI / 60.0) + (i * Math.PI / 8);
                            double spiralRadius = 0.8 - (height * 0.15);
                            
                            double x = loc.getX() + spiralRadius * Math.cos(angle);
                            double z = loc.getZ() + spiralRadius * Math.sin(angle);
                            double y = loc.getY() + height + (i * 0.1);

                            player.getWorld().spawnParticle(
                                    particleType,
                                    x, y, z,
                                    1,
                                    0, 0.05, 0,
                                    0.02
                            );
                            
                            // white sparks for mystical effect
                            if (i % 2 == 0) {
                                player.getWorld().spawnParticle(
                                        Particle.FIREWORKS_SPARK,
                                        x, y, z,
                                        1,
                                        0.05, 0.05, 0.05,
                                        0.01
                                );
                            }
                        }
                    }

                    // central pillar with white sparks (reduced)
                    double centerHeight = spiralProgress * 2.5;
                    player.getWorld().spawnParticle(
                            Particle.FIREWORKS_SPARK,
                            loc.getX(), loc.getY() + centerHeight, loc.getZ(),
                            4,  // reduced from 8 to 4
                            0.2, 0.2, 0.2,
                            0.05
                    );
                    
                    particleAngle += 18; // faster rotation
                }

                private void completeTeleport() {
                    boolean soundEnabled = plugin.getSettingsManager().getSettings(player.getUniqueId()).isSoundEnabled();
                    Particle particleType = plugin.getSettingsManager().getSettings(player.getUniqueId()).getParticle();
                    org.bukkit.Color particleColor = plugin.getSettingsManager().getSettings(player.getUniqueId()).getParticleColor();
                    
                    Location startLoc = player.getLocation();
                    
                    // white sparks on departure
                    player.getWorld().spawnParticle(
                            Particle.FIREWORKS_SPARK,
                            startLoc.clone().add(0, 1, 0),
                            40,
                            0.5, 1.0, 0.5,
                            0.1
                    );
                    
                    // white spark burst
                    player.getWorld().spawnParticle(
                            Particle.FIREWORKS_SPARK,
                            startLoc.clone().add(0, 0.5, 0),
                            30,
                            1.0, 0.5, 1.0,
                            0.1
                    );
                    
                    // colored explosion
                    for (int i = 0; i < 360; i += 10) {
                        double angle = Math.toRadians(i);
                        double x = startLoc.getX() + Math.cos(angle) * 2.0;
                        double z = startLoc.getZ() + Math.sin(angle) * 2.0;
                        player.getWorld().spawnParticle(
                                Particle.REDSTONE,
                                x, startLoc.getY() + 1, z,
                                5,
                                0, 0, 0,
                                0,
                                new Particle.DustOptions(particleColor, 3.0f)
                        );
                    }

                    if (soundEnabled) {
                        player.playSound(startLoc, Sound.ENTITY_ENDERMAN_TELEPORT, 1.5f, 0.8f);
                        player.playSound(startLoc, Sound.ENTITY_WITHER_SHOOT, 0.8f, 2.0f);
                        // portal sound removed
                    }

                    // TELEPORT
                    player.teleport(destination);

                    // spawn particles with small delay so they show up for sure
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (!player.isOnline()) return;
                        
                        Location arrivalLoc = player.getLocation();
                        
                        // SPECTACULAR ARRIVAL - all white
                        // big white spark explosion
                        arrivalLoc.getWorld().spawnParticle(
                                Particle.FIREWORKS_SPARK,
                                arrivalLoc.clone().add(0, 1, 0),
                                80,
                                0.5, 1.5, 0.5,
                                0.2
                        );
                        
                        // cloud effect for epic arrival
                        arrivalLoc.getWorld().spawnParticle(
                                Particle.CLOUD,
                                arrivalLoc.clone().add(0, 1, 0),
                                30,
                                1.0, 1.5, 1.0,
                                0.1
                        );
                        
                        // snow ring (white)
                        arrivalLoc.getWorld().spawnParticle(
                                Particle.SNOWFLAKE,
                                arrivalLoc.clone().add(0, 0.1, 0),
                                25,
                                1.5, 0.1, 1.5,
                                0.05
                        );
                        
                        // white spark pillar
                        arrivalLoc.getWorld().spawnParticle(
                                Particle.FIREWORKS_SPARK,
                                arrivalLoc.clone().add(0, 0, 0),
                                40,
                                0.5, 2.0, 0.5,
                                0.1
                        );
                        
                        // custom particles
                        arrivalLoc.getWorld().spawnParticle(
                                particleType,
                                arrivalLoc.clone().add(0, 1, 0),
                                60,
                                1.0, 1.5, 1.0,
                                0.2
                        );
                        
                        // colored circle on ground
                        for (int i = 0; i < 360; i += 5) {
                            double angle = Math.toRadians(i);
                            double x = arrivalLoc.getX() + Math.cos(angle) * 2.5;
                            double z = arrivalLoc.getZ() + Math.sin(angle) * 2.5;
                            arrivalLoc.getWorld().spawnParticle(
                                    Particle.REDSTONE,
                                    x, arrivalLoc.getY() + 0.1, z,
                                    3,
                                    0, 0, 0,
                                    0,
                                    new Particle.DustOptions(particleColor, 2.5f)
                            );
                        }
                    }, 2L); // 2 Ticks Delay für garantierte Sichtbarkeit
                }

                private boolean hasMoved() {
                    Location current = player.getLocation();
                    return current.getX() != startLocation.getX() ||
                           current.getY() != startLocation.getY() ||
                           current.getZ() != startLocation.getZ();
                }
            }.runTaskTimer(plugin, 0L, 20L); // Alle 20 Ticks (1 Sekunde)
        }

        public void cancel() {
            if (task != null) {
                task.cancel();
            }
        }
    }
}

