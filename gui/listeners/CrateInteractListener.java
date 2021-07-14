package org.cathal.ultimateEnvoy.gui.listeners;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.crates.OpenCrateStatus;
import org.cathal.ultimateEnvoy.fileSystem.Language;

public class CrateInteractListener implements Listener{

    UltimateEnvoy plugin;
    public CrateInteractListener(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }
    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        if(e.getClickedBlock() != null){
            Location loc = e.getClickedBlock().getLocation();
            e.setCancelled(plugin.getEnvoyManager().getCrateManager().getCrateSpawner().isCratePosition(loc));

            if(plugin.getEnvoyManager().getCrateManager().getCrateSpawner().isActiveCratePosition(loc)){
                e.setCancelled(true);
                Crate crate = plugin.getEnvoyManager().getCrateManager().getCrateSpawner().getCrateByLocation(loc);
                plugin.getEnvoyManager().getCrateManager().getCrateOpener().openCrate(crate,e.getPlayer(),e.getClickedBlock().getLocation());
            }
        }
    }

    @EventHandler
    public void onChestBreak(BlockBreakEvent e){
        if(plugin.getEnvoyManager().getCrateManager().getCrateSpawner().isCratePosition(e.getBlock().getLocation())){
            e.setCancelled(true);
        }
    }
}
