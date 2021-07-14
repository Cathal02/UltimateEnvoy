package org.cathal.ultimateEnvoy.gui.managers.envoys;

import net.milkbowl.vault.chat.Chat;
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
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.holders.EnvoyEditorGuiHolder;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.gui.managers.Page;

import java.util.*;

public class EnvoyEditorGui implements Listener {

    private final UltimateEnvoy plugin;
    private Map<UUID, Envoy> editingMappings = new HashMap<>();
    private final int backSlot = 9;


    public EnvoyEditorGui(UltimateEnvoy plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open(Player player, Envoy envoy){
        Inventory inv = getInventory(envoy);
        player.openInventory(inv);
        editingMappings.put(player.getUniqueId(),envoy);
    }

    private Inventory getInventory(Envoy envoy){
        Inventory inventory  = Bukkit.createInventory(new EnvoyEditorGuiHolder(), 27, "Editing: " + envoy.getName());

        ItemStack setPositionItem =  new ItemBuilder(XMaterial.REDSTONE_TORCH.parseMaterial()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "SET ENVOY GRID").toItemStack();
        ItemStack addCrate = new ItemBuilder(XMaterial.CHEST.parseMaterial()).setName(ChatColor.LIGHT_PURPLE + "" + ChatColor.BOLD + "ADD CRATES").toItemStack();
        ItemStack scheduler = new ItemBuilder(XMaterial.CLOCK.parseMaterial()).setName(ChatColor.GOLD + "" + ChatColor.BOLD + "SCHEDULE ENVOYS").toItemStack();

        inventory.setItem(0,setPositionItem);
        inventory.setItem(1, addCrate);
        inventory.setItem(2, scheduler);
        inventory.setItem(3, getCrateSpawnPositionItem(envoy));
        inventory.setItem(4, getMaximumPlayerItem(envoy));
        inventory.setItem(5, getEnvoyDurationItem(envoy));
        inventory.setItem(6, getEnvoyRefillTimer(envoy));
        inventory.setItem(7, getMinMaxCrate(envoy));
        inventory.setItem(8, getFallingCrate(envoy));
        inventory.setItem(9, getCrateHologram(envoy));

        InventoryUtils.fill(inventory);
        InventoryUtils.addBackHotbar(inventory);
        return inventory;
    }

    private ItemStack getCrateHologram(Envoy envoy) {
        
        return null;
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getWhoClicked() instanceof Player))return;
        if(!(e.getInventory().getHolder() instanceof EnvoyEditorGuiHolder))return;

        e.setCancelled(true);
        Player player = (Player)e.getWhoClicked();
        Envoy envoy = editingMappings.get(player.getUniqueId());

            switch (e.getSlot()){
                case 0:
                    plugin.getEnvoyManager().getEnvoyEditorManager().enterEditingMode(player, envoy);
                    break;
                case 1:
                    plugin.getEnvoyGuiManager().getEnvoyCrateEditor().open(player,envoy);
                    break;
                case 2:
                    plugin.getEnvoyGuiManager().getEnvoySchedulerGui().open(player,envoy);
                    break;
                case 3:
                    if(e.getClick() == ClickType.RIGHT){
                        //toggle
                        envoy.toggleCrateRandomSpawn();
                        open(player,envoy);
                    } else if(e.getClick() == ClickType.LEFT){
                        //Enter envoy editing setup
                        plugin.getEnvoyManager().getEnvoyEditorManager().enterEditingMode(player,envoy);
                    }
                    break;
                case 4:
                    if(e.getClick() == ClickType.LEFT){
                        envoy.increasePlayersRequiredToStart(1);
                    } else if(e.getClick() == ClickType.RIGHT){
                        envoy.decreasePlayersRequiredToStart(1);
                    }

                    open(player,envoy);
                    break;
                case 5:
                    new AnvilGUI.Builder()
                            .onComplete((p, t) -> {
                                try {
                                    int duration = Integer.parseInt(t);
                                    envoy.updateDuration(duration);

                                    open(p, envoy);
                                } catch (NumberFormatException err) {

                                    return AnvilGUI.Response.text("That is not a valid number!");
                                }

                                return AnvilGUI.Response.close();
                            })
                            .onClose( p -> Bukkit.getScheduler().runTask(plugin, () -> {open(p,envoy);} ))
                            .text("Current Duration: " + envoy.getEnvoyDuration() + "s")
                            .title("Enter Envoy Duration")
                            .plugin(plugin)
                            .open(player);

                    break;
                case 6:
                    if(e.getClick() == ClickType.MIDDLE){
                        new AnvilGUI.Builder()
                                .onComplete((p, t) -> {
                                    try {
                                        int duration = Integer.parseInt(t);

                                        envoy.updateEnvoyTimer(duration);

                                        open(p, envoy);
                                    } catch (NumberFormatException err) {

                                        return AnvilGUI.Response.text("That is not a valid number!");
                                    }

                                    return AnvilGUI.Response.close();
                                })
                                .onClose( p -> Bukkit.getScheduler().runTask(plugin, () -> {open(p,envoy);} ))
                                .text(String.valueOf(envoy.getRefillTimer()))
                                .title("Enter Envoy Timer")
                                .plugin(plugin)
                                .open(player);
                    }

                        else if(e.getClick()== ClickType.LEFT){
                        envoy.toggleRefillMode();
                        open(player,envoy);
                    }
                    break;
                case 7:
                    boolean isLeftClick = e.getClick() == ClickType.LEFT;
                    if(e.getClick() == ClickType.LEFT || e.getClick()==ClickType.RIGHT){

                        new AnvilGUI.Builder()
                                .onComplete((p, t) -> {
                                    try {
                                        int newValue = Integer.parseInt(t);

                                        if(isLeftClick){
                                            envoy.setMinCrates(newValue);
                                        }else{
                                            envoy.setMaxCrates(newValue);
                                        }
                                        open(p, envoy);
                                    } catch (NumberFormatException err) {

                                        return AnvilGUI.Response.text("That is not a valid number!");
                                    }

                                    return AnvilGUI.Response.close();
                                })
                                .onClose( p -> Bukkit.getScheduler().runTask(plugin, () -> {open(p,envoy);} ))
                                .text(String.valueOf(isLeftClick ? envoy.getMinCrates() : envoy.getMaxCrates()))
                                .title("Enter new value!")
                                .plugin(plugin)
                                .open(player);


                    }
                    break;
                case 8:
                    envoy.toggleFallingCrates();
                    open(player,envoy);
                    break;
                case backSlot:
                    plugin.getInventoryNavigator().handlePageChange(Page.ENVOY_EDITOR_HOME,player);
                    break;
                default: break;
            }
    }


    private ItemStack getFallingCrate(Envoy envoy) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        if(envoy.isEnableFallingCrates()){
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "" + ChatColor.BOLD + "ENABLED.");

        } else {
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.GREEN + "" + ChatColor.RED + "DISABLED.");
        }

        lore.add("");
        lore.add(ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to toggle.");
        return new ItemBuilder(XMaterial.BEACON.parseMaterial()).setLore(lore).setName(ChatColor.YELLOW + "" + ChatColor.BOLD + "FALLING CRATES").toItemStack();
    }


    private ItemStack getMinMaxCrate(Envoy envoy) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Minimum Crates to spawn: " + ChatColor.GOLD + envoy.getMinCrates());
        lore.add(ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to change.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Maximum Crates to spawn: " + ChatColor.GOLD + envoy.getMaxCrates());
        lore.add(ChatColor.GREEN + "Right-Click" + ChatColor.GRAY + " to change.");
        lore.add("");
        return new ItemBuilder(XMaterial.CHEST.parseMaterial()).setName(ChatColor.DARK_PURPLE + "" + ChatColor.BOLD + "MAXIMUM CRATES").setLore(lore).toItemStack();
    }

    private ItemStack getEnvoyRefillTimer(Envoy envoy) {
        List<String> lore = new ArrayList<>();
        lore.add("");

        if(envoy.getRefillMode() == EnvoyRefillMode.ALL_CRATES){
            lore.add(ChatColor.GRAY + "Refill Mode: " + ChatColor.LIGHT_PURPLE + "Refill Crates Together.");
            lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "This will refill crates together, repeating at the end of the timer.");
        }else {
            lore.add(ChatColor.GRAY + "Envoy Refill Mode: " + ChatColor.LIGHT_PURPLE + "Refill Crates Individually.");
            lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "This will refill crates individually, starting a timer after it has been opened.");
        }
        lore.add(ChatColor.DARK_GREEN + "Left-Click" + ChatColor.GRAY + " to toggle.");

        lore.add("");
        lore.add(ChatColor.GRAY + "Envoy Refill Timer: " + ChatColor.LIGHT_PURPLE + envoy.getRefillTimer() + "s");
        lore.add(ChatColor.DARK_GREEN + "Middle-Click" + ChatColor.GRAY + " to edit.");
        return new ItemBuilder(XMaterial.CLOCK.parseMaterial()).setLore(lore).setName(ChatColor.AQUA + "" + ChatColor.BOLD + "ENVOY REFILL").toItemStack();
    }

    private ItemStack getEnvoyDurationItem(Envoy envoy) {
        List<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GRAY + "Envoy Duration: " + ChatColor.GOLD + envoy.getEnvoyDuration() + "s");
        lore.add("");
        lore.add(ChatColor.GREEN + "Left-Click" + ChatColor.GRAY + " to edit.");
        return new ItemBuilder(XMaterial.CLOCK.parseMaterial()).setLore(lore).setName(ChatColor.BLUE + "" + ChatColor.BOLD + "ENVOY DURATION").toItemStack();
    }

    @EventHandler
    public void inventoryClose(InventoryCloseEvent e){
        if(e.getInventory().getHolder() instanceof EnvoyEditorGuiHolder){
            editingMappings.remove(e.getPlayer().getUniqueId());
        }
    }

    private ItemStack getMaximumPlayerItem(Envoy envoy) {
        ArrayList<String> lore = new ArrayList<>();
        lore.add("");
        lore.add(ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to increase the players required.");
        lore.add(ChatColor.RED + "Right-Click " + ChatColor.GRAY + "to decrease the players required.");
        lore.add("");
        lore.add(ChatColor.GRAY + "Current players required to start envoy: " + ChatColor.AQUA + "" + ChatColor.BOLD + envoy.getPlayersRequiredToStartEnvoy());

        return new ItemBuilder(XMaterial.PLAYER_HEAD.parseItem()).setName(ChatColor.AQUA + "" + ChatColor.BOLD + "MINIMUM PLAYERS").setLore(lore).toItemStack();
    }

    private ItemStack getCrateSpawnPositionItem(Envoy envoy) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to set crate spawn positions (if enabled)");
        lore.add(ChatColor.LIGHT_PURPLE + "Right-Click " + ChatColor.GRAY + "to toggle this option");
        lore.add("");
        lore.add(ChatColor.GRAY + "" + ChatColor.ITALIC + "Random spawn positions will be used if this option is disabled.");
        lore.add(" ");
        if(!envoy.getIsEnvoyUsingRandomCrateSpawnPositions()){
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.GREEN + " " + ChatColor.BOLD + "ENABLED");
        } else {
            lore.add(ChatColor.GRAY + "Status: " + ChatColor.RED + " " + ChatColor.BOLD + "DISABLED");
        }

        return new ItemBuilder(XMaterial.BEACON.parseMaterial()).setName(ChatColor.YELLOW + "" + ChatColor.BOLD + "Set Crate Spawn Positions").setLore(lore).toItemStack();

    }



}
