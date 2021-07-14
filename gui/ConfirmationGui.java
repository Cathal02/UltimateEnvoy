package org.cathal.ultimateEnvoy.gui;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.gui.holders.ConfirmationHolder;

public class ConfirmationGui implements Listener{

    private Consumer<Player> confirmConsumer;
    private Consumer<Player> declineConsumer;
    private Player player;
    private UltimateEnvoy plugin;
    public ConfirmationGui(Player player,UltimateEnvoy plugin){
        this.player = player;
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        player.closeInventory();
        open();
    }

    private void open(){
        Inventory inventory = Bukkit.createInventory(new ConfirmationHolder(),27, "Are you sure?");

        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Be careful, this action is irreversible!" +
                "");
        inventory.setItem(12, new ItemBuilder(XMaterial.EMERALD_BLOCK.parseMaterial()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "CONFIRM").setLore(lore).toItemStack());
        inventory.setItem(14, new ItemBuilder(XMaterial.REDSTONE_BLOCK.parseMaterial()).setName(ChatColor.RED + "" + ChatColor.BOLD + "CANCEL").toItemStack());
        InventoryUtils.fill(inventory);
        player.openInventory(inventory);
    }

    public ConfirmationGui onConfirm(Consumer<Player> onConfirm){
        this.confirmConsumer = onConfirm;
        return this;
    }

    public ConfirmationGui onDecline(Consumer<Player> onDecline){
        this.declineConsumer = onDecline;
        return this;

    }


    @EventHandler
    private void onConfirmClick(InventoryClickEvent e){
        if(!(e.getInventory().getHolder() instanceof ConfirmationHolder))return;
        if(!(e.getWhoClicked() instanceof Player))return;
        Player player = (Player)e.getWhoClicked();

            if(e.getRawSlot() == 12){ // Clicked confirm
                if(confirmConsumer == null){
                    player.closeInventory();
                    return;
                }

                confirmConsumer.accept(player);
            } else if(e.getRawSlot() == 14){ // Clicked decline
                if(declineConsumer == null){
                    player.closeInventory();
                    return;
                }

                declineConsumer.accept(player);
            }
    }

    @EventHandler
    private void onInventoryClose(InventoryCloseEvent e){
        if(!(e.getInventory().getHolder() instanceof ConfirmationHolder))return;
        HandlerList.unregisterAll(this);
    }
}
