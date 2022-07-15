package dev.cgs.maprecenter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.ArrayList;
import java.util.List;

public class RecenterCommand implements CommandExecutor {

        public void recenterMap(Player player, boolean isMainHand) {
            ItemStack map = isMainHand ? player.getInventory().getItemInMainHand() : player.getInventory().getItemInOffHand();
            ItemMeta meta = map.getItemMeta();
            if (!(meta instanceof MapMeta)) {
                player.sendMessage("the map you're holding is borked!");
                return;
            }
            MapMeta mapMeta = (MapMeta)meta;
            MapView old = mapMeta.getMapView();
            if (old.isLocked()) {
                player.sendMessage("sorry, that map is already locked!");
                return;
            }
            MapView mapView = Bukkit.createMap(player.getWorld());
            mapView.setScale(old.getScale());
            mapView.setUnlimitedTracking(old.isUnlimitedTracking());
            mapView.setTrackingPosition(old.isTrackingPosition());
            mapView.setCenterX(player.getLocation().getBlockX());
            mapView.setCenterZ(player.getLocation().getBlockZ());
            mapMeta.setMapView(mapView);
            List<String> lore = new ArrayList<String>();
            if (mapView.isUnlimitedTracking()) {
                lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "This map has improved tracking abilities.");
            }
            lore.add(ChatColor.RESET + "" + ChatColor.RED + "This map does not align to the normal grid.");
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Center X: " + player.getLocation().getBlockX());
            lore.add(ChatColor.RESET + "" + ChatColor.GRAY + "Center Z: " + player.getLocation().getBlockZ());
            mapMeta.setLore(lore);
            map.setItemMeta(mapMeta);
            if (isMainHand)
                player.getInventory().setItemInMainHand(map);
            else
                player.getInventory().setItemInOffHand(map);
            player.sendMessage("Map recentered on your current position!");
        }

        // This method is called, when somebody uses our command
        @Override
        public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!(sender instanceof Player)) {
                return false;
            }
            Player player = (Player) sender;
            ItemStack mainHand = player.getInventory().getItemInMainHand();
            ItemStack offHand = player.getInventory().getItemInOffHand();

            if(mainHand.getType() == Material.FILLED_MAP && offHand.getType() == Material.FILLED_MAP) {
                player.sendMessage("I don't know which map you want me to recenter :(");
                return true;
            }
            if(mainHand.getType() == Material.FILLED_MAP) {
                this.recenterMap(player, true);
                return true;
            }
            else if(offHand.getType() == Material.FILLED_MAP) {
                this.recenterMap(player, false);
                return true;
            }
            return false;
        }
    }
