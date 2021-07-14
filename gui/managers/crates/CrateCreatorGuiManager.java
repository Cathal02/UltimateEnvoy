package org.cathal.ultimateEnvoy.gui.managers.crates;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.gui.holders.CrateCreatorHolder;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.gui.managers.Page;

import java.util.*;

public class CrateCreatorGuiManager implements Listener {

    private UltimateEnvoy plugin;
    private Map<Integer, Crate> crateMappings = new HashMap<>();

    public CrateCreatorGuiManager(UltimateEnvoy plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void open(Player player){
        player.openInventory(getInventory());

    }

    private Inventory getInventory(){
        Crate[] crates = plugin.getEnvoyManager().getCrateManager().getAllCrates();
        int size = InventoryUtils.roundUpToInvSizeWithHotbar(crates.length +1);

        Inventory inventory = Bukkit.createInventory(new CrateCreatorHolder(), size, ChatColor.GREEN + "Create/Edit Crates");
        createCrateGui(inventory, crates, size);
        InventoryUtils.addBackHotbar(inventory);
        InventoryUtils.fill(inventory);
        return inventory;
    }

    //TODO: Add page scrolling
    private void createCrateGui(Inventory inventory, Crate[] crates, int size) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GREEN + "Left-Click to edit the crate settings.");
        lore.add(ChatColor.RED + "Right-Click to remove the crate.");
        lore.add(" ");

        for(int i = 0; i < crates.length; i++){
            inventory.setItem(i, new ItemBuilder(crates[i].getCrateItem()).setName(ChatColor.WHITE + ChatColor.translateAlternateColorCodes('&', crates[i].getName())).setLore(lore).toItemStack());
            crateMappings.put(i, crates[i]);
        }


        inventory.setItem(size-1, new ItemBuilder(XMaterial.LIME_STAINED_GLASS_PANE.parseItem()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "Create new Crate").toItemStack());
    }


    private Crate getCrateFromIndex(Integer i){
        return crateMappings.get(i);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getInventory().getHolder() instanceof CrateCreatorHolder))return;

            if(e.getCurrentItem() == null)return;
            if(!(e.getWhoClicked() instanceof Player))return;
            e.setCancelled(true);

            Player myPlayer = (Player)e.getWhoClicked();
            if(e.getRawSlot() == e.getInventory().getSize()-1){
                new AnvilGUI.Builder()
                        .onComplete((player, text) -> {
                            player.sendMessage(ChatColor.GREEN + "Crate name set to: " + text);
                            plugin.getEnvoyManager().getCrateManager().createCrate(text);
                            open(myPlayer);
                            return AnvilGUI.Response.close();
                        })
                        .text("Enter Crate Name")
                        .title("Enter your answer.")
                        .plugin(plugin)
                        .open(myPlayer);
            }else if(getCrateFromIndex(e.getRawSlot()) != null){
                plugin.getEnvoyGuiManager().getCrateEditorGui().open(myPlayer, getCrateFromIndex(e.getRawSlot()));
            } else if(e.getRawSlot() == e.getClickedInventory().getSize()-9){
                plugin.getInventoryNavigator().handlePageChange(Page.HOME,myPlayer);
            }

    }


}

