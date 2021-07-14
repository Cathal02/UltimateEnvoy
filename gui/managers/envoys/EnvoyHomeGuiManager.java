package org.cathal.ultimateEnvoy.gui.managers.envoys;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.holders.EnvoyCreatorHolder;
import org.cathal.ultimateEnvoy.gui.ConfirmationGui;
import org.cathal.ultimateEnvoy.gui.managers.Page;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class EnvoyHomeGuiManager implements Listener {

    UltimateEnvoy plugin;
    private Map<Integer, Integer> envoyIdMappings = new HashMap<>();

    public EnvoyHomeGuiManager(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void open(Player player){
        player.openInventory(getInventory());
    }


    private Inventory getInventory(){
        Envoy[] envoys = plugin.getEnvoyManager().getAllEnvoys();
        Inventory inventory = Bukkit.createInventory(new EnvoyCreatorHolder(), InventoryUtils.roundUpToInvSizeWithHotbar(envoys.length+1), "Envoys");

        envoyIdMappings.clear();
        for(int i = 0; i < envoys.length; i++){
            if(i < inventory.getSize()){
                List<String> lore = new ArrayList<>();
                lore.add(" ");
                lore.add(ChatColor.GREEN + "Left click " + ChatColor.GRAY + "to edit envoy");
                lore.add(ChatColor.RED + "Right click " + ChatColor.GRAY +  "to remove envoy");
                lore.add(" ");
                inventory.setItem(i,new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()).setName(ChatColor.GREEN + ""  + ChatColor.BOLD  + envoys[i].getName()).setLore(lore).toItemStack());

                envoyIdMappings.put(i,envoys[i].getId());
            }
        }

        InventoryUtils.fill(inventory);
        inventory.setItem(inventory.getSize()-10, new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Create new Envoy").toItemStack());

        InventoryUtils.addBackHotbar(inventory);
        return inventory;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(e.getClickedInventory() != null && e.getClickedInventory().getHolder() instanceof EnvoyCreatorHolder){
            e.setCancelled(true);
            Player myPlayer = (Player)e.getWhoClicked();
            // Wants to create a new envoy
            if(e.getRawSlot() == e.getInventory().getSize()-10){
                new AnvilGUI.Builder()
                        .onComplete((player, text) -> {
                            player.sendMessage(ChatColor.GREEN + "Envoy name set to: " + text);
                            plugin.getEnvoyManager().createEnvoy(text);
                            open(myPlayer);
                            return AnvilGUI.Response.close();
                        })
                        .text("Enter Envoy Name")
                        .title("Enter your answer.")
                        .plugin(plugin)
                        .open(myPlayer);
            } else if(envoyIdMappings.containsKey(e.getSlot())){

                // Wants to edit an envoy

                Envoy envoy = plugin.getEnvoyManager().getEnvoyByID(envoyIdMappings.get(e.getSlot()));
                if(e.getClick() == ClickType.LEFT){
                    plugin.getEnvoyGuiManager().getEnvoyEditorGui().open(myPlayer,envoy);
                } else if(e.getClick() == ClickType.RIGHT){

                    // Opens confirmation GUI
                    new ConfirmationGui(myPlayer, plugin).onConfirm(player -> {
                        plugin.getEnvoyManager().deleteEnvoy(envoy);
                        player.sendMessage(ChatColor.GREEN + "Envoy  removed.");
                        open(player);
                    }).onDecline(this::open);
                }
            } else if(e.getRawSlot()==e.getInventory().getSize()-9){
                plugin.getInventoryNavigator().handlePageChange(Page.HOME,myPlayer);
            }
        }
    }
}
