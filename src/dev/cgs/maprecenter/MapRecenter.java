package dev.cgs.maprecenter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class MapRecenter extends JavaPlugin implements Listener {

    @EventHandler
    public void onCraft(SmithItemEvent e) {
        if(e.getCurrentItem().getType() != Material.FILLED_MAP) {
            return;
        }
        ItemMeta meta = e.getCurrentItem().getItemMeta();
        if (!(meta instanceof MapMeta)) {
            return;
        }
        MapMeta mapMeta = (MapMeta)meta;
        mapMeta.getMapView().setUnlimitedTracking(true);
        e.getCurrentItem().setItemMeta(mapMeta);
    }
    @EventHandler
    public void onPrepare(PrepareSmithingEvent e) {
        if (e.getInventory().contains(Material.FILLED_MAP)) {
            if (e.getInventory().contains(Material.GOLD_INGOT, 1)) {
                ItemStack is = e.getInventory().getItem(0).clone();
                ItemMeta meta = is.getItemMeta();
                if (!(meta instanceof MapMeta)) {
                    return;
                }
                MapMeta mapMeta = (MapMeta)meta;
                if (mapMeta.getMapView().isUnlimitedTracking()) {
                    e.setResult(null);
                    return;
                }
                List<String> lore = meta.getLore();
                if (lore == null) lore = new ArrayList<String>();
                lore.add(ChatColor.RESET + "" + ChatColor.GOLD + "This map has improved tracking abilities.");
                meta.setLore(lore);
                is.setItemMeta(meta);
                e.setResult(is);
            }
            else {
                e.setResult(e.getInventory().getItem(0));
            }
        }
    }

    private final NamespacedKey recipeKey = new NamespacedKey(this, "cgs-map-upgrade");

    @Override
    public void onEnable() {
        SmithingRecipe recipe = new SmithingRecipe(recipeKey,
                new ItemStack(Material.AIR), // any material seems fine
                new RecipeChoice.MaterialChoice(Material.FILLED_MAP),
                new RecipeChoice.MaterialChoice(Material.GOLD_INGOT)
        );
        Bukkit.addRecipe(recipe);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getCommand("recenter").setExecutor(new RecenterCommand());
        this.getLogger().info("MapRecenter is now enabled!");
    }

    @Override
    public void onDisable(){
        Bukkit.removeRecipe(recipeKey);
        this.getLogger().info("MapRecenter is now disabled.");
    }
}
