package org.cathal.ultimateEnvoy.gui.managers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.gui.managers.envoys.*;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.gui.holders.EnvoyGuiHolder;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.managers.crates.CrateCreatorGuiManager;
import org.cathal.ultimateEnvoy.gui.managers.crates.CrateEditorGuiManager;
import org.cathal.ultimateEnvoy.gui.managers.rewards.CrateRewardCreatorGuiManager;
import org.cathal.ultimateEnvoy.gui.managers.rewards.CrateRewardEditorGuiManager;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.event.Listener;
public class EnvoyGuiManager implements Listener{

    private final UltimateEnvoy plugin;
    private final CrateCreatorGuiManager crateCreatorGui;
    private final CrateEditorGuiManager crateEditorGui;
    private final CrateRewardCreatorGuiManager crateRewardCreatorGuiManager;
    private final CrateRewardEditorGuiManager crateRewardEditorGuiManager;
    private final EnvoyHomeGuiManager envoyHomeGuiManager;
    private final EnvoyCrateEditorGui envoyCrateEditor;
    private final EnvoyEditorGui envoyEditorGui;
    private final EnvoySchedulerGui envoySchedulerGui;
    private final  EnvoySchedulerEditGui envoySchedulerEditGui;

    public EnvoyGuiManager(UltimateEnvoy plugin){
        this.plugin = plugin;
        crateCreatorGui = new CrateCreatorGuiManager(plugin);
        crateEditorGui = new CrateEditorGuiManager(plugin);
        crateRewardCreatorGuiManager = new CrateRewardCreatorGuiManager(plugin);
        crateRewardEditorGuiManager = new CrateRewardEditorGuiManager(plugin);
        envoyHomeGuiManager = new EnvoyHomeGuiManager(plugin);
        envoyCrateEditor = new EnvoyCrateEditorGui(plugin);
        envoyEditorGui = new EnvoyEditorGui(plugin);
        envoySchedulerGui = new EnvoySchedulerGui(plugin);
        envoySchedulerEditGui = new EnvoySchedulerEditGui(plugin);
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }

    public void open(Player player){
        player.openInventory(getInventory());
    }

    private Inventory getInventory(){

        Inventory inv = Bukkit.createInventory(new EnvoyGuiHolder(), 27,ChatColor.RED + "Envoy Admin" );

        List<String> crateLore = new ArrayList<>();
        crateLore.add(0,"");
        crateLore.add(1,ChatColor.RESET + "" + ChatColor.GRAY +  "Create and edit envoy crates.");
        crateLore.add(2,"");

        ItemStack crateItem = new ItemBuilder(Material.ENDER_CHEST).setName(ChatColor.GREEN  + "Create/Edit Crates").setLore(crateLore).toItemStack();

        inv.setItem(11, crateItem);

        List<String> envoyLore = new ArrayList<>();
        envoyLore.add(0,"");
        envoyLore.add(1,ChatColor.RESET + "" + ChatColor.GRAY +  "Create and edit envoys");
        envoyLore.add(2,"");

        ItemStack envoyItem = new ItemBuilder(XMaterial.REDSTONE_TORCH.parseMaterial()).setName(ChatColor.GREEN + "Create/Edit Envoys").setLore(envoyLore).toItemStack();

        inv.setItem(13,envoyItem);

        InventoryUtils.fill(inv);
        return inv;
    }

    public CrateCreatorGuiManager getCrateCreatorGui() {
        return crateCreatorGui;
    }
    public CrateEditorGuiManager getCrateEditorGui() {
        return crateEditorGui;
    }
    public CrateRewardCreatorGuiManager getCrateRewardCreatorGuiManager() {
        return crateRewardCreatorGuiManager;
    }
    public CrateRewardEditorGuiManager getCrateRewardEditorGuiManager(){return crateRewardEditorGuiManager;}
    public EnvoyHomeGuiManager getEnvoyHomeGuiManager(){return envoyHomeGuiManager;}
    public EnvoyCrateEditorGui getEnvoyCrateEditor(){return  envoyCrateEditor;}
    public EnvoySchedulerGui getEnvoySchedulerGui(){return  envoySchedulerGui;}
    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getInventory().getHolder() instanceof EnvoyGuiHolder))return;
            if(e.getCurrentItem() == null)return;
            if(!(e.getWhoClicked() instanceof Player)) return;
            e.setCancelled(true);

            Player player = (Player)e.getWhoClicked();
            if(e.getRawSlot() ==11){
                crateCreatorGui.open(player);
            } else if(e.getRawSlot() ==13){
                envoyHomeGuiManager.open(player);
            }
        }



    public EnvoyEditorGui getEnvoyEditorGui() {
        return envoyEditorGui;
    }

    public EnvoySchedulerEditGui getEnvoySchedulerEditGui() {
        return envoySchedulerEditGui;
    }
}

