package org.cathal.ultimateEnvoy.envoys.crates;

import org.apache.commons.math3.util.Pair;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.events.CrateOpenEvent;
import org.cathal.ultimateEnvoy.utils.XMaterial;
import org.cathal.ultimateEnvoy.utils.XSound;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.event.Listener;

public class CrateSpawner implements Listener{

    // Boolean represents whether or not crate can be opened. True -> can be.
    private final Map<Location, Pair<Crate,Boolean>> spawnedCrates = new HashMap<>();
    private final Map<Location, Crate> landLocations = new HashMap<>();

    UltimateEnvoy plugin;

    public CrateSpawner(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    public boolean spawnFallingCrate(Location loc, Crate crate) {

        if(!isValidSpawnlocation(loc)) return false;
        loc.getChunk().load();
        FallingBlock fallingBlock = loc.getWorld().spawnFallingBlock(new Location(loc.getWorld(), loc.getX(), loc.getY() + 50, loc.getZ()), XMaterial.BEACON.parseMaterial(), (byte) 0);
        fallingBlock.setDropItem(false);
        fallingBlock.setFallDistance(20);
        fallingBlock.setVelocity(new Vector(0, -1.5, 0));

        landLocations.put(loc,crate);

        return true;
    }

    public void crateLanded(Location landLocation){
        Crate crate = landLocations.get(landLocation);
        if(crate != null){
            landLocations.remove(landLocation);
            spawnActualCrate(landLocation,crate);
        }
    }

    public void resetCratesOpenedStatus(){
        spawnedCrates.replaceAll((loc, v) -> new Pair<>(spawnedCrates.get(loc).getKey(), true));
    }

    public void resetCrateOpenedStatus(Crate crate,Location loc){
        if(!spawnedCrates.containsKey(loc))return;
        spawnedCrates.put(loc, new Pair<>(crate,true));
    }


     public boolean spawnActualCrate(Location loc, Crate crate) {
         if(!isValidSpawnlocation(loc)) return false;

         Block block = loc.getBlock();

        XSound.play(loc, "EXPLODE");

        block.setType(crate.getCrateItem().getType());
        spawnedCrates.put(loc, new Pair<>(crate,true));
        plugin.getHologramManager().createHologram(loc, crate.getName());
        return true;

     }

    public boolean validateFallingCrateLandLocation(Location location) {

        return landLocations.containsKey(location);
    }

    private boolean isValidSpawnlocation(Location location){
        for(Location loc : landLocations.keySet()){
            if(loc.distanceSquared(location) <= 1){
                return false;
            }
        }
        return true;
    }


    public boolean isActiveCratePosition(Location location) {
        return spawnedCrates.containsKey(location) && spawnedCrates.get(location).getValue();
    }

    public boolean isCratePosition(Location location){
        return spawnedCrates.containsKey(location);



    }

    public Crate getCrateByLocation(Location loc){
        return spawnedCrates.get(loc).getKey();
    }


    @EventHandler
    public void onCrateOpen(CrateOpenEvent e) {
        spawnedCrates.get(e.getLocation()).getKey().handleCrateOpened(e.getPlayer());
        spawnedCrates.put(e.getLocation(), new Pair<>(spawnedCrates.get(e.getLocation()).getKey(), false));
    }
}
