package org.cathal.ultimateEnvoy.envoys;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.utils.BlockPlacer;
import org.cathal.ultimateEnvoy.utils.XMaterial;

public class EnvoyEditorManager implements Listener{

    private Map<UUID, Envoy> editingMappings = new HashMap<>();
    private Map<Location, Material> previousLocationMaterials = new HashMap<>();

    private final EnvoyEditPhysicalOptionsManager envoyPlayerManager;
    private final EnvoyCrateSpawnPositionManager envoyCrateSpawnPositionManager;

    private final UltimateEnvoy plugin;
        public EnvoyEditorManager(UltimateEnvoy plugin) {
            envoyPlayerManager = new EnvoyEditPhysicalOptionsManager();
            envoyCrateSpawnPositionManager = new EnvoyCrateSpawnPositionManager(plugin);

            this.plugin = plugin;

            plugin.getServer().getPluginManager().registerEvents(this,plugin);

        }

    public void enterEditingMode(Player player, Envoy envoy){
        editingMappings.put(player.getUniqueId(),envoy);

        if(envoy.getEdgeOne() != null && envoy.getEdgeTwo() != null){
            previousLocationMaterials.put(envoy.getEdgeOne(),envoy.getEdgeOne().getBlock().getType());
            previousLocationMaterials.put(envoy.getEdgeTwo(),envoy.getEdgeTwo().getBlock().getType());
        }
        envoyPlayerManager.enterPlayer(player);
        envoyCrateSpawnPositionManager.enterEditingMode(envoy,player);

        player.setGameMode(GameMode.CREATIVE);
    }

    public void exitEditingMode(Player player){
            if(player == null)return;

        envoyPlayerManager.restorePlayer(player);
        envoyCrateSpawnPositionManager.exitEditingMode(player);

        plugin.getEnvoyGuiManager().getEnvoyEditorGui().open(player,editingMappings.get(player.getUniqueId()));
        editingMappings.remove(player.getUniqueId());
    }

    public void handlePlayerInteract(PlayerInteractEvent e){
        Player player = e.getPlayer();
        Envoy envoy = editingMappings.get(player.getUniqueId());

        if(e.getAction() == Action.LEFT_CLICK_BLOCK){
            envoyCrateSpawnPositionManager.blockRemoved(envoy,e);
        }
        switch (e.getPlayer().getInventory().getHeldItemSlot()){
            // Set location 1
            case 0:
                e.setCancelled(true);


                if(player.isSneaking()){
                    if((e.getAction() == Action.LEFT_CLICK_AIR) || (e.getAction() == Action.LEFT_CLICK_BLOCK)){

                        if(envoy.getEdgeOne() != null){

                            player.teleport(envoy.getEdgeOne());
                            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "TELEPORTED" + ChatColor.GREEN + "to edge one.");
                            return;

                        }
                    } else if((e.getAction() == Action.RIGHT_CLICK_AIR) || (e.getAction() == Action.RIGHT_CLICK_BLOCK)){
                        if(envoy.getEdgeTwo() != null){
                            player.teleport(envoy.getEdgeTwo());
                            player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "TELEPORTED" + ChatColor.GREEN + "to edge two.");
                            return;
                        }
                    }
                }

                if(e.getClickedBlock() == null)return;
                Block block = e.getClickedBlock();
                if(e.getAction() == Action.LEFT_CLICK_BLOCK){


                    // Replaces previous edge with correct block
                    updatePreviousLocation(player,envoy.getEdgeOne());
                    BlockPlacer.simulateBlockPlace(player,block.getLocation(),XMaterial.EMERALD_BLOCK.parseMaterial());


                    envoy.setEdgeOne(block.getLocation());
                    previousLocationMaterials.put(block.getLocation(),block.getType());

                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "First edge set.");

                }else if (e.getAction() == Action.RIGHT_CLICK_BLOCK){

                    updatePreviousLocation(player,envoy.getEdgeTwo());
                    BlockPlacer.simulateBlockPlace(player,block.getLocation(),XMaterial.LAPIS_BLOCK.parseMaterial());
                    previousLocationMaterials.put(block.getLocation(),block.getType());

                    envoy.setEdgeTwo(block.getLocation());
                    player.sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + "Second edge set.");

                }


                break;
            case 1:
                if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
                    envoyCrateSpawnPositionManager.blockPlaced(e);
                }
                break;
            case 8:
                e.setCancelled(true);

                exitEditingMode(e.getPlayer());
                break;
        }
    }


    @EventHandler
    public void onLogout(PlayerQuitEvent e){
            if(!editingMappings.containsKey(e.getPlayer().getUniqueId())) return;
            editingMappings.remove(e.getPlayer().getUniqueId());
            envoyCrateSpawnPositionManager.exitEditingMode(e.getPlayer());
    }

    public void updatePreviousLocation(Player player,Location location){
        if(previousLocationMaterials.containsKey(location)){
            BlockPlacer.simulateBlockPlace(player,location,previousLocationMaterials.get(location));
            previousLocationMaterials.remove(location);
        }else{
            BlockPlacer.simulateBlockPlace(player,location,XMaterial.AIR.parseMaterial());
        }

    }
    public boolean playerIsEditing(Player player) {
        return editingMappings.containsKey(player.getUniqueId());
    }

    public Envoy getEnvoy(UUID uuid) {
        return editingMappings.get(uuid);
    }

    public void serverClose() {
        envoyPlayerManager.restoreAll();
    }
}
