package org.cathal.ultimateEnvoy.gui.managers.envoys;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.EnvoyDate;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.holders.EnvoySchedulerEditHolder;

import java.util.*;

import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.gui.managers.Page;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.utils.TimeUtils;
import org.cathal.ultimateEnvoy.utils.XMaterial;

public class EnvoySchedulerEditGui implements Listener{

    private Map<UUID, EnvoyDate> envoyDateMappings = new HashMap<>();
    private Map<UUID,Envoy> envoyMappings = new HashMap<>();
    private UltimateEnvoy plugin;
    public EnvoySchedulerEditGui(UltimateEnvoy plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void open(Player player, EnvoyDate date, Envoy envoy){
        if(HandlerList.getHandlerLists().contains(this)){
        }
        player.openInventory(getInventory(date));
        envoyDateMappings.put(player.getUniqueId(),date);
        envoyMappings.put(player.getUniqueId(),envoy);

    }

    private Inventory getInventory(EnvoyDate date) {
        Inventory inventory = Bukkit.createInventory(new EnvoySchedulerEditHolder(),18,"Edit Envoy Time");

        for(int i = 0; i <7; i++){
                if(date.hasDay(i)){
                    inventory.setItem(i, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()).setName(ChatColor.GOLD + "" + ChatColor.BOLD + TimeUtils.parseDay(i)).toItemStack());
                }else{
                    inventory.setItem(i, new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseMaterial()).setName(ChatColor.GOLD + "" + ChatColor.BOLD + TimeUtils.parseDay(i)).toItemStack());
                }
        }

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Current Spawn Time: " + ChatColor.GOLD + TimeUtils.formatDate(date));
        inventory.setItem(7, new ItemBuilder(XMaterial.CLOCK.parseMaterial()).setName(ChatColor.GOLD + "" + ChatColor.BOLD + "Update Envoy Time.").setLore(lore).toItemStack());

        InventoryUtils.fill(inventory);
        InventoryUtils.addBackHotbar(inventory);
        return inventory;
    }

    @EventHandler
    public void onInteract(InventoryClickEvent e){
        if(!(e.getInventory().getHolder() instanceof EnvoySchedulerEditHolder))return;
        EnvoyDate date = envoyDateMappings.get(e.getWhoClicked().getUniqueId());

        if(date==null)return;
        if(e.getRawSlot()<7){

            if(date.hasDay(e.getRawSlot())){
                date.removeDay(e.getRawSlot());
            } else {
                date.addDay(e.getRawSlot());
            }

            open((Player)e.getWhoClicked(),date,envoyMappings.get(e.getWhoClicked().getUniqueId()));

        }else if(e.getRawSlot()==7){

            new AnvilGUI.Builder()
            .onComplete((p, text) -> {

                    EnvoyDate dateInput = TimeUtils.parseTime(text);

                    if(dateInput ==null){
                    return AnvilGUI.Response.text("That is not a valid number!");
                    }

                    if(TimeUtils.checkForDuplicates(envoyMappings.get(p.getUniqueId()).getEnvoyDates(),dateInput)){
                    return AnvilGUI.Response.text("This Envoy Time already exists!");
                    }


                    date.update(date);
                    open(p,date,envoyMappings.get(p.getUniqueId()));

                    return AnvilGUI.Response.close();
            })
            .onClose(p -> {
                       Bukkit.getScheduler().runTask(plugin, () -> {open(p,date,envoyMappings.get(p.getUniqueId()));});
            })
            .text(TimeUtils.formatHour(date.getHour(),date.getMinute()))
            .title("Enter new envoy date.")
            .plugin(plugin)
            .open((Player)e.getWhoClicked());

        } else if(e.getRawSlot()==e.getInventory().getSize()-9){
            plugin.getInventoryNavigator().handlePageChange(Page.ENVOY_EDITOR_SCHEDULER,(Player)e.getWhoClicked(),envoyMappings.get(e.getWhoClicked().getUniqueId()));
        }
    }
}




