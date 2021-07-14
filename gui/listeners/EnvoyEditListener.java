package org.cathal.ultimateEnvoy.gui.listeners;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

public class EnvoyEditListener implements Listener{

    UltimateEnvoy plugin;
    public EnvoyEditListener(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if(plugin.getEnvoyManager().getEnvoyEditorManager().playerIsEditing(e.getPlayer())){
            plugin.getEnvoyManager().getEnvoyEditorManager().handlePlayerInteract(e);
        }
    }

}
