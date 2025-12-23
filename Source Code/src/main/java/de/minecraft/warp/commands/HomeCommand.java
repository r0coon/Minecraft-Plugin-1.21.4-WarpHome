package de.minecraft.warp.commands;

import de.minecraft.warp.WarpHome;
import de.minecraft.warp.gui.MainGUI;
import de.minecraft.warp.model.Home;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HomeCommand implements CommandExecutor {

    private final WarpHome plugin;

    public HomeCommand(WarpHome plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§c⚠ §7Players only!");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("fancyhomes.home")) {
            player.sendMessage(plugin.getMessage("no-permission"));
            return true;
        }

        // if name provided, tp directly
        if (args.length > 0) {
            // join args to support spaces in names
            String homeName = String.join(" ", args);
            Home home = plugin.getHomeManager().getHome(player.getUniqueId(), homeName);
            
            if (home == null) {
                String message = plugin.getMessage("home-not-found").replace("{name}", homeName);
                player.sendMessage(message);
                return true;
            }
            
            // tp directly
            plugin.getTeleportManager().startTeleport(player, home.getLocation(), homeName);
            return true;
        }

        // no name, open gui
        MainGUI gui = new MainGUI(plugin, player);
        plugin.getServer().getPluginManager().registerEvents(gui, plugin);
        gui.open();

        return true;
    }
}

