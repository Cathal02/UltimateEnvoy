package org.cathal.ultimateEnvoy.gui.managers.rewards;


import java.util.logging.Level;

import net.milkbowl.vault.chat.Chat;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.managers.Page;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;
import org.cathal.ultimateEnvoy.gui.holders.CrateRewardEditorHolder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.bukkit.event.Listener;

import java.util.*;
import java.util.logging.Logger;

public class CrateRewardEditorGuiManager implements Listener {
    UltimateEnvoy plugin;

    private Map<UUID, CrateReward> rewardMappings = new HashMap<>();
    public CrateRewardEditorGuiManager(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }

    public void open(Player player, CrateReward crateReward){
        rewardMappings.put(player.getUniqueId(),crateReward);

        player.openInventory(getInventory(crateReward));


    }

    private Inventory getInventory(CrateReward reward) {


        Inventory inventory = Bukkit.createInventory(new CrateRewardEditorHolder(),18, ChatColor.GRAY + "Reward Editor");
        if(reward==null){
            Logger.getLogger("minecraft").log(Level.SEVERE, "[UltimateEnvoy] Could not find reward item.");
            return inventory;
        }
        inventory.setItem(0,getRewardItem(reward));
        setChanceItem(inventory,1,reward);

        InventoryUtils.addBackHotbar(inventory);
        return inventory;
    }

    private ItemStack getRewardItem(CrateReward reward) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to edit item amount.");

        return new ItemBuilder(reward.getRewardItem().getType(),reward.getRewardItem().getAmount()).setLore(lore).setName(ChatColor.AQUA + "" + ChatColor.BOLD + "SET AMOUNT").toItemStack();

    }

    private void setChanceItem(Inventory inventory, int i, CrateReward reward) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to change the probability.");

        ItemStack item = new ItemBuilder(XMaterial.ENCHANTING_TABLE.parseMaterial()).setName(ChatColor.GRAY + "Chance: " + ChatColor.GREEN + reward.getChance()).setLore(lore).toItemStack();
        inventory.setItem(i,item);
    }


    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof CrateRewardEditorHolder)) return;
        if (!(e.getWhoClicked() instanceof Player)) return;
        e.setCancelled(true);

        UUID playerUUID = e.getWhoClicked().getUniqueId();

        switch (e.getRawSlot()){
            case 0:
                new AnvilGUI.Builder()
                        .onComplete((player, text) -> {
                            try {
                                rewardMappings.get(playerUUID).getRewardItem().setAmount(Integer.parseInt(text));
                                return AnvilGUI.Response.close();
                            } catch (NullPointerException | NumberFormatException error){
                                error.printStackTrace();
                                return AnvilGUI.Response.text("That is not a valid number!");
                            }
                        })
                        .onClose(player -> Bukkit.getScheduler().runTask(plugin, () -> open(player,rewardMappings.get(playerUUID))))
                        .text("Amount: " + rewardMappings.get(playerUUID).getRewardItem().getAmount())
                        .plugin(plugin)
                        .open((Player)e.getWhoClicked());
                break;
            case 1:
                if(rewardMappings.get(playerUUID) == null)return;

                new AnvilGUI.Builder()
                        .onComplete((player, text) -> {
                            try {
                                rewardMappings.get(playerUUID).setChance(Double.parseDouble(text));

                                return AnvilGUI.Response.close();
                            } catch (NullPointerException | NumberFormatException error){
                                error.printStackTrace();
                                return AnvilGUI.Response.text("That is not a valid number!");
                            }
                        })
                        .onClose(player -> Bukkit.getScheduler().runTask(plugin, () -> open(player,rewardMappings.get(playerUUID))))
                        .text("Probability: " + rewardMappings.get(playerUUID).getChance())
                        .plugin(plugin)
                        .open((Player)e.getWhoClicked());
                break;
            case 9:
                plugin.getInventoryNavigator().handlePageChange(Page.CRATE_EDITOR_HOME,(Player)e.getWhoClicked());
                break;
            default:
                break;
        }
    }

}
