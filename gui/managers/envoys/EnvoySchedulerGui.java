package org.cathal.ultimateEnvoy.gui.managers.envoys;

import net.milkbowl.vault.chat.Chat;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.Envoy;
import org.cathal.ultimateEnvoy.envoys.EnvoyDate;
import org.cathal.ultimateEnvoy.gui.ConfirmationGui;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;
import org.cathal.ultimateEnvoy.gui.holders.EnvoySchedulerDailyHolder;
import org.cathal.ultimateEnvoy.gui.managers.Page;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.utils.TimeUtils;
import org.cathal.ultimateEnvoy.utils.XMaterial;

import java.sql.Time;
import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.*;
import org.bukkit.event.Listener;
public class EnvoySchedulerGui implements Listener{

    UltimateEnvoy plugin;
    private Map<UUID, Envoy> mappings = new HashMap<>();
    private Map<Integer, EnvoyDate> envoyDateGuiMappings = new HashMap<>();

    public EnvoySchedulerGui(UltimateEnvoy plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public void open(Player player, Envoy envoy) {
        mappings.put(player.getUniqueId(),envoy);

        player.openInventory(getInventory(envoy));
    }

    private Inventory getInventory(Envoy envoy) {
        List<EnvoyDate> dates = envoy.getEnvoyDates();
        Inventory inventory = Bukkit.createInventory(new EnvoySchedulerDailyHolder(), InventoryUtils.roundUpToInvSizeWithHotbar(dates.size()+1), "Envoy Scheduler");

        envoyDateGuiMappings.clear();
        for(EnvoyDate date : dates){
            envoyDateGuiMappings.put(inventory.firstEmpty(),date);
            inventory.setItem(inventory.firstEmpty(), getEnvoyDateItem(date));
        }

        inventory.setItem(inventory.getSize()-10, new ItemBuilder(XMaterial.GREEN_STAINED_GLASS_PANE.parseMaterial()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "ADD NEW TIME").toItemStack());
        InventoryUtils.fill(inventory);
        InventoryUtils.addBackHotbar(inventory);
        return inventory;
    }

    private ItemStack getEnvoyDateItem(EnvoyDate date){
        List<String> lore = new ArrayList<>();
        lore.add("");

        if(date.getDays().size()!=7){
            lore.add(ChatColor.GRAY + "Active Days: ");
            for(int i = 0; i < date.getDays().size(); i++){
                lore.add(ChatColor.GRAY + "- " + ChatColor.GOLD + TimeUtils.parseDay(i));
            }
        }else if(date.getDays().size()==7){
            lore.add(ChatColor.GRAY + "Active Days: " + ChatColor.GOLD + "ALL");
            lore.add("");
        } else {
            lore.add(ChatColor.GRAY + "Active Days: " + ChatColor.GOLD + "NONE");
            lore.add("");
        }

        lore.add(ChatColor.GREEN + "Left-Click " + ChatColor.GRAY + "to edit time.");
        lore.add(ChatColor.RED + "Right-Click " + ChatColor.GRAY + "to remove time.");

        return new ItemBuilder(XMaterial.CLOCK.parseMaterial()).setName(ChatColor.GREEN + TimeUtils.formatDate(date)).setLore(lore).toItemStack();
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if(!(e.getInventory().getHolder() instanceof EnvoySchedulerDailyHolder))return;

        Player player = (Player)e.getWhoClicked();
        Envoy playerEnvoy = mappings.get(player.getUniqueId());
        EnvoyDate selectedDate = envoyDateGuiMappings.get(e.getRawSlot());

        if(envoyDateGuiMappings.containsKey(e.getRawSlot())){
            // Edit envoy Date
            if(e.getClick() == ClickType.LEFT){
                plugin.getEnvoyGuiManager().getEnvoySchedulerEditGui().open(player,selectedDate,playerEnvoy);

            } else if(e.getClick() == ClickType.RIGHT){
                // Delete Envoy Date
                new ConfirmationGui(player,plugin)
                        .onDecline(p -> plugin.getInventoryNavigator().handlePageChange(Page.ENVOY_EDITOR_SCHEDULER,p,playerEnvoy))
                        .onConfirm(p -> {
                            playerEnvoy.removeDate(envoyDateGuiMappings.get(e.getRawSlot()));
                            envoyDateGuiMappings.remove(e.getRawSlot());
                            open(player,playerEnvoy);
                        });
            }

        } else if(e.getRawSlot() ==e.getInventory().getSize()-10){ // Clicked add new item
            new AnvilGUI.Builder()
                    .onComplete((p, text) -> {

                        EnvoyDate date = TimeUtils.parseTime(text);

                        if(date ==null){
                            return AnvilGUI.Response.text("That is not a valid number!");
                        }
                        if(TimeUtils.checkForDuplicates(mappings.get(p.getUniqueId()).getEnvoyDates(),date)){
                            return AnvilGUI.Response.text("This Envoy Time already exists!");
                        }


                        mappings.get(p.getUniqueId()).addDate(date);
                        open(p,playerEnvoy);

                        return AnvilGUI.Response.close();
                    })
                    .onClose( p -> Bukkit.getScheduler().runTask(plugin, () -> {open(p,playerEnvoy);} ))

                    .text("Envoy Time e.g. 13:00")
                    .title("Enter new envoy date.")
                    .plugin(plugin)
                    .open(player);
        } else if(e.getRawSlot()==e.getInventory().getSize()-9){ // GO BACK
            plugin.getInventoryNavigator().handlePageChange(Page.ENVOY_EDITOR,(Player)e.getWhoClicked(),mappings.get(e.getWhoClicked().getUniqueId()));
        }
    }

}
