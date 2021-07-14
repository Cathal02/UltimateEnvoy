package org.cathal.ultimateEnvoy.gui.managers.envoys;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.holders.CratePickerHolder;

import java.util.*;

import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.gui.holders.EnvoyCrateEditorHolder;
import org.cathal.ultimateEnvoy.gui.managers.Page;

public class EnvoyCrateEditorGui implements Listener {

    UltimateEnvoy plugin;
    private Map<UUID, Envoy> envoyMappings = new HashMap<>();
    private Map<Integer, Crate> cratePickerMappings = new HashMap<>();
    private Map<Integer, Crate> selectedCratesMappings = new HashMap<>();

    public EnvoyCrateEditorGui(UltimateEnvoy plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }

    public void open(Player player, Envoy envoy){
        envoyMappings.put(player.getUniqueId(),envoy);
        player.openInventory(getInventory(envoy));
    }

    public Inventory getInventory(Envoy envoy){

        //Opens a menu with all active crates with a button to add more
        List<Crate> crates = envoy.getCrates();
        Inventory inv = Bukkit.createInventory(new EnvoyCrateEditorHolder(), InventoryUtils.roundUpToInvSizeWithHotbar(crates.size()+1), "Add crates to " + envoy.getName());

        if(crates.size() == 0){
            for(int i = 0; i < 9; i++){
                inv.setItem(i, new ItemBuilder(XMaterial.GRAY_STAINED_GLASS_PANE.parseMaterial()).setName(ChatColor.RED + " " + ChatColor.BOLD + "NO CRATES.").toItemStack());
            }
        }


        for(int i = 0; i <crates.size();i++){
            List<String> lore = new ArrayList<>();
            lore.add(" ");
            lore.add(ChatColor.GREEN + "Left-Click" + ChatColor.GRAY +" to edit crate globally.");
            lore.add(ChatColor.RED +"Right-Click" +ChatColor.GRAY + " to remove crate from this envoy.");
            lore.add(" ");
            lore.add(ChatColor.GOLD + "Middle-Click" + ChatColor.GRAY +" to edit crate probability.");
            lore.add(ChatColor.GRAY + "Current Probability: " + ChatColor.GOLD + envoy.getCrateProbability(crates.get(i)));
            lore.add("");

            inv.setItem(i, new ItemBuilder(crates.get(i).getCrateItem()).setLore(lore).toItemStack());
            selectedCratesMappings.put(i,crates.get(i));

        }
        InventoryUtils.fill(inv);
        InventoryUtils.addBackHotbar(inv);
        inv.setItem(inv.getSize()-1, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()).setName(ChatColor.GREEN + " " + ChatColor.BOLD + "ADD CRATE").toItemStack());

        return inv;
    }

    public void openCratePicker(Player player, Envoy envoy){
        //Opens menu to add more crates
        Crate[] crates = plugin.getEnvoyManager().getCrateManager().getAllCrates();
        Inventory inv = Bukkit.createInventory(new CratePickerHolder(), InventoryUtils.roundUpToInvSizeWithHotbar(crates.length), "Pick a crate");


        for(int i = 0;i<crates.length; i++){
            inv.setItem(i, crates[i].getCrateItem());
            cratePickerMappings.put(i,crates[i]);
        }

        InventoryUtils.addBackHotbar(inv);
        player.openInventory(inv);
    }

    private void handleCrateEditorClick(InventoryClickEvent e) {
        Player player = (Player)e.getWhoClicked();
        Envoy envoy = envoyMappings.get(player.getUniqueId());

        if(e.getSlot() == e.getInventory().getSize()-1){
            openCratePicker(player, envoy);
        } else if(e.getSlot() == e.getInventory().getSize()-9){
            plugin.getInventoryNavigator().handlePageChange(Page.ENVOY_EDITOR,player,envoy);
        } else {
            // Clicked a crate
            Crate crate = selectedCratesMappings.get(e.getSlot());
            if(crate==null)return;

            if(e.getClick() == ClickType.LEFT){
                plugin.getInventoryNavigator().handlePageChange(Page.CRATE_EDITOR, player, crate);
            } else if(e.getClick() == ClickType.RIGHT){
                envoy.removeCrate(crate);
                open(player,envoy);
            } else if(e.getClick() == ClickType.MIDDLE){
                new AnvilGUI.Builder()
                        .onComplete((p, t) -> {
                            try {
                                double probability = Double.parseDouble(t);
                                envoy.updateCrateProbability(crate,probability);

                                open(p, envoy);
                            } catch (NumberFormatException err) {

                                return AnvilGUI.Response.text("That is not a valid number!");
                            }

                            return AnvilGUI.Response.close();
                        })
                        .text(String.valueOf(envoy.getCrateProbability(crate)))
                        .title("Enter Crate Probability")
                        .plugin(plugin)
                        .open(player);
            }
        }
    }

    private void handleCratePickerHolder(InventoryClickEvent e) {
        Crate crate = cratePickerMappings.get(e.getSlot());
        Player player = (Player)e.getWhoClicked();
        Envoy envoy = envoyMappings.get(e.getWhoClicked().getUniqueId());
        if(crate != null){
            envoyMappings.get(e.getWhoClicked().getUniqueId());
            if(envoy!= null){
                envoy.addCrate(crate);
                open((Player)e.getWhoClicked(),envoy);
            }
        } else if(e.getRawSlot() == e.getInventory().getSize()-9){
            plugin.getInventoryNavigator().handlePageChange(Page.ENVOY_EDITOR_CHEST,player,envoy);
        }
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player))return;

        if(e.getInventory().getHolder() instanceof EnvoyCrateEditorHolder){
            // Handles a click in the main envoy crate editor menu
            handleCrateEditorClick(e);
            e.setCancelled(true);

        } else if(e.getInventory().getHolder() instanceof CratePickerHolder){
            e.setCancelled(true);

            // Handles a click when selecting a crate
            handleCratePickerHolder(e);
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        envoyMappings.remove(e.getPlayer().getUniqueId());
    }

}
