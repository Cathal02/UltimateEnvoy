package org.cathal.ultimateEnvoy.gui.listeners;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;
import org.cathal.ultimateEnvoy.gui.holders.CancelClickHolder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XSound;

public class InventoryClickListener implements Listener{

    UltimateEnvoy plugin;

    public InventoryClickListener(UltimateEnvoy plugin){
        this.plugin =plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }
    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() == null)return;
        if(e.getClickedInventory().getHolder() == null)return;

        if(e.getClickedInventory().getHolder() instanceof CancelClickHolder && !(e.getClickedInventory() instanceof PlayerInventory)){
            e.setCancelled(true);
            XSound.play((Player)e.getWhoClicked(), "UI_BUTTON_CLICK");
        }
    }
}
