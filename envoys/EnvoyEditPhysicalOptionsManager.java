package org.cathal.ultimateEnvoy.envoys;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.cathal.ultimateEnvoy.utils.ItemBuilder;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.gui.InventoryUtils;

import java.util.*;

public class EnvoyEditPhysicalOptionsManager {

    private Map<UUID, EnvoyPlayerData> playerData = new HashMap<>();

    public void enterPlayer(Player player){

        EnvoyPlayerData data = new EnvoyPlayerData(player.getInventory().getContents().clone(),player.getGameMode(),player.getLocation());
        playerData.put(player.getUniqueId(),data);
        player.closeInventory();
        player.getInventory().clear();
        fillInventory(player);

    }

    public void restorePlayer(Player player){
        EnvoyPlayerData data = playerData.get(player.getUniqueId());
        if(data == null)return;

        player.getInventory().setContents(data.getItems());
        player.setGameMode(data.getGameMode());
        player.teleport(data.getLocation());
    }

    private void fillInventory(Player player) {
        List<String> lore = new ArrayList<>();
        lore.add(" ");
        lore.add(ChatColor.LIGHT_PURPLE + "Right/Left click on a block to set the ");
        lore.add(ChatColor.LIGHT_PURPLE + "edges of the square in which crates will spawn");
        lore.add("");
        lore.add(ChatColor.GREEN  + "Shift-Left-Click " + ChatColor.GRAY +"to teleport to first edge.");
        lore.add(ChatColor.GOLD  + "Shift-Right-Click " + ChatColor.GRAY +"to teleport to second edge.");
        lore.add(" ");
        ItemStack setPositionItem =  new ItemBuilder(XMaterial.REDSTONE_TORCH.parseMaterial()).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "SET GRID POSITION").setLore(lore).toItemStack();
        player.getInventory().setItem(0,setPositionItem);

        List<String> crateLore = new ArrayList<>();
        crateLore.add(" ");
        crateLore.add(ChatColor.LIGHT_PURPLE + "Place a beacon down where you want crates to spawn.");
        ItemStack setCratePositionItem = new ItemBuilder((XMaterial.BEACON.parseMaterial())).setName(ChatColor.GREEN + "" + ChatColor.BOLD + "SET CRATE SPAWN POSITIONS").setLore(lore).toItemStack();
        player.getInventory().setItem(1,setCratePositionItem);
        player.getInventory().setItem(8, new ItemBuilder(XMaterial.BARRIER.parseMaterial()).setName(ChatColor.RED + "" + ChatColor.BOLD  + "EXIT").toItemStack());
        InventoryUtils.fillPlayerHotbar(player.getInventory());
    }


    public void restoreAll() {
        for(UUID uuid : playerData.keySet()){
            restorePlayer(Objects.requireNonNull(Bukkit.getPlayer(uuid)));
        }
    }
}

class EnvoyPlayerData {
    ItemStack[] items;
    GameMode gameMode;
    Location location;

    public EnvoyPlayerData(ItemStack[] contents, GameMode gameMode, Location location){
        this.items = contents.clone();
        this.gameMode = gameMode;
        this.location = location;
    }

    public ItemStack[] getItems(){return items;}
    public GameMode getGameMode(){return gameMode;}
    public Location getLocation(){return location;}
}