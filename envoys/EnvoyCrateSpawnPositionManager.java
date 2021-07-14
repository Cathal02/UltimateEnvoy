package org.cathal.ultimateEnvoy.envoys;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.utils.XSound;

import java.util.*;

import static org.cathal.ultimateEnvoy.utils.BlockPlacer.simulateBlockPlace;

public class EnvoyCrateSpawnPositionManager{


    UltimateEnvoy plugin;
    private Map<UUID,List<Location>> blockChangeLocations = new HashMap<>();
    private Map<UUID, Envoy> envoyMap = new HashMap<>();
    public EnvoyCrateSpawnPositionManager(UltimateEnvoy plugin){
        this.plugin = plugin;
    }

    public void enterEditingMode(Envoy envoy,Player player){
        blockChangeLocations.put(player.getUniqueId(), envoy.getCrateSpawnLocations());
        for(Location loc : envoy.getCrateSpawnLocations()){
            simulateBlockPlace(player,loc,XMaterial.BEACON.parseMaterial());
        }
         simulateBlockPlace(player,envoy.getEdgeTwo(),XMaterial.EMERALD_BLOCK.parseMaterial());
         simulateBlockPlace(player,envoy.getEdgeOne(), XMaterial.EMERALD_BLOCK.parseMaterial());

         envoyMap.put(player.getUniqueId(),envoy);
    }

    public void exitEditingMode(Player player){
        resetBlocks(player);
        blockChangeLocations.remove(player.getUniqueId());
        Envoy envoy = envoyMap.get(player.getUniqueId());
        if(envoy != null){
            simulateBlockPlace(player,envoy.getEdgeTwo(),XMaterial.AIR.parseMaterial());
            simulateBlockPlace(player,envoy.getEdgeOne(), XMaterial.AIR.parseMaterial());
        }
        envoyMap.remove(player.getUniqueId());

    }

    private void resetBlocks(Player player) {
        if(player == null)return;
        for(Location loc : blockChangeLocations.get(player.getUniqueId())){
            simulateBlockPlace(player,loc,XMaterial.AIR.parseMaterial());
        }

    }

    public void blockRemoved(Envoy envoy,PlayerInteractEvent e){
        if(e.getClickedBlock()==null)return;

        Location loc = e.getClickedBlock().getLocation();
        Player player = e.getPlayer();
        if(!blockChangeLocations.get(player.getUniqueId()).contains(loc))return;

        blockChangeLocations.get(player.getUniqueId()).remove(loc);
        simulateBlockPlace(player,loc,XMaterial.AIR.parseMaterial());
        envoy.removeCrateSpawnLocation(loc);

        player.sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Crate spawn location removed.");
        XSound.play(player,XSound.ENTITY_VILLAGER_HURT.name());

    }

    public void blockPlaced(PlayerInteractEvent e) {
        e.setCancelled(true);
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();

        Envoy envoy = plugin.getEnvoyManager().getEnvoyEditorManager().getEnvoy(uuid);
        if(envoy==null)return;
        if(!blockChangeLocations.containsKey(uuid))return;
        Location loc =  e.getClickedBlock().getRelative(e.getBlockFace()).getLocation();

        envoy.addCrateSpawnLocation(loc);
        blockChangeLocations.get(uuid).add(loc);

        simulateBlockPlace(player,loc,XMaterial.BEACON.parseMaterial());
        player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Crate spawn location added.");
        XSound.play(player,XSound.BLOCK_NOTE_BLOCK_PLING.name());

    }


}
