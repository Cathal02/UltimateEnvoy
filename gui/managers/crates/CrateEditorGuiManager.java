package org.cathal.ultimateEnvoy.gui.managers.crates;

import net.milkbowl.vault.chat.Chat;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.holders.CrateEditorHodler;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.gui.managers.Page;

import java.util.*;

public class CrateEditorGuiManager implements Listener {

    UltimateEnvoy plugin;
    private Map<UUID, Crate> currentlyEditing = new HashMap<>();
    public CrateEditorGuiManager(UltimateEnvoy plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);

    }

    public void open(Player player, Crate crate){
        Inventory inventory = Bukkit.createInventory(new CrateEditorHodler(),18, ChatColor.DARK_GREEN + "Edit crate: " + ChatColor.GRAY +  crate.getName());

        player.openInventory(inventory);
        currentlyEditing.put(player.getUniqueId(),crate);

        inventory.setItem(0, new ItemBuilder(crate.getCrateItem().getType())
                .setName((crate.getName() == null) ? " " : ChatColor.translateAlternateColorCodes('&',crate.getName()))
                .setLore(ChatColor.GRAY + "Left-Click to edit name.")
                .toItemStack());

        List<String> crateRewardsLore = new ArrayList<>();
        crateRewardsLore.add("");
        crateRewardsLore.add((ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to edit rewards"));

        inventory.setItem(1, new ItemBuilder(XMaterial.DIAMOND_BLOCK.parseItem())
                .setName(ChatColor.AQUA + "" + ChatColor.BOLD  + "CRATE REWARDS")
                .setLore(crateRewardsLore)
                .toItemStack());

        insertOpenMethodItem(crate, inventory);

        List<String> crateItemLore = new ArrayList<>();
        crateItemLore.add("");
        crateItemLore.add(ChatColor.GRAY + "Drop the new crate item on this slot.");

        inventory.setItem(3, new ItemBuilder(crate.getCrateItem())
                 .setName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "CRATE ITEM")
                 .setLore(crateItemLore)
                 .toItemStack());

        inventory.setItem(4, getAddPermissionItem(crate));
        inventory.setItem(5, getAddBalanceItem(crate));
        InventoryUtils.addBackHotbar(inventory);
    }

    private ItemStack getAddPermissionItem(Crate crate) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY  +"Current Permission: " + ChatColor.LIGHT_PURPLE + crate.getRequiredPermission());
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Set to 'none' for no permission.");
        lore.add("");
        lore.add(ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to change permission");
        return new ItemBuilder(XMaterial.PAPER.parseMaterial()).setName(ChatColor.BLUE + "" + ChatColor.BOLD + "SET PERMISSION").setLore(lore).toItemStack();
    }

    private ItemStack getAddBalanceItem(Crate crate) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY  +"Current Balance Required: " + ChatColor.LIGHT_PURPLE + "$" + crate.getRequiredBalance());
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Set to 0 for no balance requirement.");
        lore.add("");
        lore.add(ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to change value.");
        return new ItemBuilder(XMaterial.GOLD_INGOT.parseMaterial()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "SET BALANCE REQUIRED").setLore(lore).toItemStack();
    }

    private void insertOpenMethodItem(Crate crate, Inventory inventory) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.DARK_GREEN + "Left-Click " + ChatColor.GRAY + "to cycle open method.");
        switch (crate.getCrateOpenMethod()){
            case OPEN_CHEST:
                lore.add(ChatColor.GRAY + "Current Method: " + ChatColor.LIGHT_PURPLE + "Open Chest");
                break;
            case DROP_ON_FLOOR:
                lore.add(ChatColor.GRAY + "Current Method: "  + ChatColor.LIGHT_PURPLE +  "Drop on Floor");
                break;
            case INSERT_TO_INVENTORY:
            default:
                lore.add(ChatColor.GRAY + "Current Method: " + ChatColor.LIGHT_PURPLE +  "Insert to Inventory");

                break;
        }
        lore.add(" ");
        inventory.setItem(2, new ItemBuilder(XMaterial.OAK_TRAPDOOR.parseItem())
                .setName(ChatColor.GREEN + "" + ChatColor.BOLD + "OPEN METHOD")
                .setLore(lore).toItemStack());

    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        if(!(e.getInventory().getHolder() instanceof CrateEditorHodler))return;

        if(!(currentlyEditing.containsKey(e.getWhoClicked().getUniqueId())))return;
        if(!(e.getClickedInventory() instanceof PlayerInventory)){
            e.setCancelled(true);
        }
                Crate crate = currentlyEditing.get(e.getWhoClicked().getUniqueId());
                UUID playerUUID = e.getWhoClicked().getUniqueId();

                switch (e.getRawSlot()){
                    case 0:
                        new AnvilGUI.Builder()
                                .onComplete((player, text) -> {
                                    plugin.getEnvoyManager().getCrateManager().editCrateName(crate, text);
                                    open(player, crate);
                                    return AnvilGUI.Response.close();
                                })
                                .onClose(player -> Bukkit.getScheduler().runTask(plugin, () -> open(player,crate)))
                                .text("Enter New Crate Name")
                                .plugin(plugin)
                                .open((Player)e.getWhoClicked());
                        break;
                    case 1:
                        plugin.getEnvoyGuiManager().getCrateRewardCreatorGuiManager().open((Player)e.getWhoClicked(), currentlyEditing.get(playerUUID));
                        break;
                    case 2:
                        plugin.getEnvoyManager().getCrateManager().cycleCrateOpenMethod(currentlyEditing.get(playerUUID));
                        open((Player)e.getWhoClicked(),currentlyEditing.get(playerUUID));
                        break;
                    case 3:
                        if(e.getCursor() != null && e.getCursor().getType().isBlock() && e.getCursor().getType() != XMaterial.AIR.parseMaterial()){
                            Material newItem = e.getCursor().getType();
                            plugin.getEnvoyManager().getCrateManager().updateCrateItem(currentlyEditing.get(playerUUID), newItem);
                            e.setCancelled(true);
                            open((Player)e.getWhoClicked(),currentlyEditing.get(playerUUID));
                        }
                        break;
                    case 4:
                        new AnvilGUI.Builder()
                                .onComplete((player, text) -> {
                                    plugin.getEnvoyManager().getCrateManager().setCratePermission(crate, text);
                                    open(player, crate);
                                    return AnvilGUI.Response.close();
                                })
                                .onClose(player -> Bukkit.getScheduler().runTask(plugin, () -> open(player,crate)))
                                .text("Enter Crate Permission")
                                .plugin(plugin)
                                .open((Player)e.getWhoClicked());
                        break;
                    case 5:
                        new AnvilGUI.Builder()
                                .onComplete((player, text) -> {
                                    try {
                                        plugin.getEnvoyManager().getCrateManager().setCrateBalance(crate,Double.parseDouble(text));
                                        open(player, crate);
                                        return AnvilGUI.Response.close();
                                    } catch (NullPointerException | NumberFormatException error){
                                        return AnvilGUI.Response.text("That is not a valid number!");
                                    }
                                })
                                .onClose(player -> Bukkit.getScheduler().runTask(plugin, () -> open(player,crate)))
                                .text("Enter Required Crate Balance")
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

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e){
        if(!(e.getInventory().getHolder() instanceof CrateEditorHodler))return;
        currentlyEditing.remove(e.getPlayer().getUniqueId());
    }


}
