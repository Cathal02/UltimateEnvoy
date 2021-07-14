package org.cathal.ultimateEnvoy.gui.listeners;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;

public class CrateFallListener implements Listener{

    UltimateEnvoy plugin;
    public CrateFallListener(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);


    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent e){
        //TODO: Check if there is an active envoy
        if(plugin.getEnvoyManager().getCrateManager().getCrateSpawner().validateFallingCrateLandLocation(e.getBlock().getLocation())){
            e.setCancelled(true);

            plugin.getEnvoyManager().getCrateManager().getCrateSpawner().crateLanded(e.getBlock().getLocation());

        }
    }
}
