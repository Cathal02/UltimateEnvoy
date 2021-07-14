package org.cathal.ultimateEnvoy.gui.managers.rewards;

import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.crates.CrateReward;
import org.cathal.ultimateEnvoy.gui.holders.CrateRewardCreatorHolder;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.gui.managers.Page;

import java.util.*;

public class CrateRewardCreatorGuiManager implements Listener {
    UltimateEnvoy plugin;

    private Map<UUID, Crate> editorMappings = new HashMap<>();
    private Map<Integer, CrateReward> crateRewardMappings = new HashMap<>();

    public CrateRewardCreatorGuiManager(UltimateEnvoy plugin){
        this.plugin =plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }

    public void open(Player player, Crate crate){

        // Delayed to allow for removing of previous editor mapping from close event
        new BukkitRunnable() {
            @Override
            public void run() {
                editorMappings.put(player.getUniqueId(),crate);

            }
        }.runTaskLater(plugin,1);

        player.openInventory(getInventory(crate));
    }

    private Inventory getInventory(Crate crate) {
        CrateReward[] rewards = crate.getCrateRewards();
        int size = InventoryUtils.roundUpToInvSizeWithHotbar(rewards.length);
        Inventory inventory = Bukkit.createInventory(new CrateRewardCreatorHolder(), size, ChatColor.GRAY + "Crate Rewards: " + ChatColor.GREEN + crate.getName());

        if(rewards.length > 0){
            for(int i = 0; i < rewards.length; i++ ){
                ItemStack item = rewards[i].getRewardItem();
                List<String> lore = new ArrayList<>();
                lore.add("");
                lore.add(ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to edit reward.");
                lore.add(ChatColor.GOLD + "Middle-Click " + ChatColor.GRAY + "to set reward probability.");

                lore.add("");
                lore.add(ChatColor.GRAY  + "Probability: " + ChatColor.GOLD + "" + ChatColor.BOLD + rewards[i].getChance());
                lore.add("");
                inventory.setItem(i, new ItemBuilder(item.getType()).setLore(lore).toItemStack());
                crateRewardMappings.put(i,rewards[i]);
            }
        } else {
            for(int i = 0; i < inventory.getSize(); i++){
                inventory.setItem(i, new ItemBuilder((XMaterial.GRAY_STAINED_GLASS_PANE.parseItem()))
                        .setName(ChatColor.GRAY + "No rewards present")
                .setLore(ChatColor.LIGHT_PURPLE + "Click an item in your inventory which you want to add as a reward.").toItemStack());
            }
        }

        InventoryUtils.addBackHotbar(inventory);
        InventoryUtils.fill(inventory, new ItemBuilder(XMaterial.BLACK_STAINED_GLASS_PANE.parseMaterial()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "CLICK ITEM IN INVETORY TO ADD.").toItemStack());
        return inventory;
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(e.getInventory().getHolder() instanceof CrateRewardCreatorHolder){
            Player player = (Player)e.getWhoClicked();

            if(e.getClickedInventory() instanceof PlayerInventory){
                if(e.getCurrentItem() == null) return;
                if(e.getCurrentItem().getType() == XMaterial.AIR.parseMaterial())return;
                e.setCancelled(true);


                plugin.getRewardManager().createNewReward(editorMappings.get(e.getWhoClicked().getUniqueId()),e.getCurrentItem());
                open(player, editorMappings.get(e.getWhoClicked().getUniqueId()));
                e.setCancelled(true);

            }else  if(e.getRawSlot() < crateRewardMappings.keySet().size() && crateRewardMappings.get(e.getRawSlot()) != null){ //Check to see if we clicked on a reward
                e.setCancelled(true);

                if(e.getClick() == ClickType.MIDDLE){
                    CrateReward reward = crateRewardMappings.get(e.getRawSlot());
                    new AnvilGUI.Builder()
                            .onComplete((p, text) -> {
                                try {
                                    reward.setChance(Double.parseDouble(text));

                                    return AnvilGUI.Response.close();
                                } catch (NullPointerException | NumberFormatException error){
                                    error.printStackTrace();
                                    return AnvilGUI.Response.text("That is not a valid number!");
                                }
                            })
                            .onClose(p -> Bukkit.getScheduler().runTask(plugin, () -> open(player,editorMappings.get(player.getUniqueId()))))
                            .text("Probability: " + reward.getChance())
                            .plugin(plugin)
                            .open(player);
                    return;
                }
                plugin.getEnvoyGuiManager().getCrateRewardEditorGuiManager().open(player, crateRewardMappings.get(e.getRawSlot()));
            } else if(e.getRawSlot() == e.getInventory().getSize()-9){
                plugin.getInventoryNavigator().handlePageChange(Page.CRATE_EDITOR,player,editorMappings.get(player.getUniqueId()));
            }

        }
    }


}
