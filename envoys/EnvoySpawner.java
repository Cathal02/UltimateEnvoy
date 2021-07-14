package org.cathal.ultimateEnvoy.envoys;

import com.google.common.base.Preconditions;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.fileSystem.Language;
import org.cathal.ultimateEnvoy.gui.managers.envoys.EnvoyRefillMode;
import org.cathal.ultimateEnvoy.gui.managers.envoys.EnvoyRefiller;
import org.cathal.ultimateEnvoy.envoys.events.EnvoyEndEvent;
import org.cathal.ultimateEnvoy.envoys.events.EnvoyStartEvent;
import org.cathal.ultimateEnvoy.utils.XMaterial;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class EnvoySpawner {

    UltimateEnvoy plugin;
    private List<ActiveEnvoy> activeEnvoys = new ArrayList<>();
    private final EnvoyRefiller envoyRefiller;

    public EnvoySpawner(UltimateEnvoy plugin, EnvoyRefiller refiller){
        this.plugin =plugin;
         envoyRefiller = refiller;
    }

    public void spawnEnvoy(Envoy envoy) {
        plugin.getServer().broadcastMessage(Language.translateEnvoyMessage(Language.getString("envoySpawnMessage", true), envoy.getName(), 5));

        Crate[] cratesToSpawn = envoy.getCratesToSpawn();
        if (cratesToSpawn.length < 1) {
            Logger.getLogger("minecraft").log(Level.WARNING, "[UltimateEnvoy] No crates set for envoy " + envoy.getName());
            return;
        }

        ActiveEnvoy activeEnvoy = new ActiveEnvoy(plugin,envoy);
        activeEnvoys.add(activeEnvoy);

        for (Crate crate : cratesToSpawn) {
            Location loc;
            if (envoy.getIsEnvoyUsingRandomCrateSpawnPositions() || envoy.getCrateSpawnLocations().size() <1) {
                loc = getRandomLocation(envoy.getEdgeOne(), envoy.getEdgeTwo());
            } else {
                loc = getRandomLocationFromSpawnList(envoy);
            }

            int counter = 0;
            boolean spawned = false;
            while (!spawned) {
                counter++;
                if(envoy.isEnableFallingCrates()){
                    spawned = plugin.getEnvoyManager().getCrateManager().getCrateSpawner().spawnFallingCrate(loc, crate);
                } else{
                    spawned = plugin.getEnvoyManager().getCrateManager().getCrateSpawner().spawnActualCrate(loc, crate);
                }

                if(spawned){
                    activeEnvoy.addSpawnLocation(loc);
                }
                if (counter > 15) break;
            }
        }

        Bukkit.getPluginManager().callEvent(new EnvoyStartEvent(activeEnvoy));

        new BukkitRunnable(){
            @Override
            public void run(){
                Bukkit.getPluginManager().callEvent(new EnvoyEndEvent(activeEnvoy));
                cleanupEnvoy(activeEnvoy);

                // This will run one second before envoy should end to avoid clashing!
            }
        }.runTaskLater(plugin,(envoy.getEnvoyDuration()*20)-40);
    }


    //TODO: Cleanup holograms
    public void cleanupEnvoy(ActiveEnvoy envoy){
        for(Location loc : envoy.getSpawnLocations()){
            if(loc.getBlock().getType() == XMaterial.CHEST.parseMaterial()){
                loc.getBlock().setType(XMaterial.AIR.parseMaterial());
                plugin.getHologramManager().removeHologram(loc);
            }
        }

        activeEnvoys.remove(envoy);
        Language.broadcast(Language.getEnvoyOverMessage(envoy.getEnvoy().getName()));
    }

    public void cleanupAllEnvoys(){
        activeEnvoys.forEach(this::cleanupEnvoy);
    }

    private Location getRandomLocationFromSpawnList(Envoy envoy) {
        int spawnLocation = ThreadLocalRandom.current().nextInt(0,envoy.getCrateSpawnLocations().size());
        return envoy.getCrateSpawnLocations().get(spawnLocation);
    }

    private Location getRandomLocation(Location loc1, Location loc2) {
        Preconditions.checkArgument(loc1.getWorld() == loc2.getWorld());
        int minX = Math.min(loc1.getBlockX(), loc2.getBlockX());
        int minZ = Math.min(loc1.getBlockZ(), loc2.getBlockZ());

        int maxX = Math.max(loc1.getBlockX(), loc2.getBlockX());
        int maxZ = Math.max(loc1.getBlockZ(), loc2.getBlockZ());

        int xCoord = randomInt(minX,maxX);
        int zCoord = randomInt(minZ,maxZ);
        int yCoord = loc1.getWorld().getHighestBlockYAt(xCoord,zCoord);

        return new Location(loc1.getWorld(),xCoord,yCoord,zCoord);
    }

    private int randomInt(int min, int max) {
        return min + ThreadLocalRandom.current().nextInt(Math.abs(max - min + 1));
    }

    public List<ActiveEnvoy> getActiveEnvoys(){
        return activeEnvoys;
    }

    public ActiveEnvoy getEnvoyFromCrateSpawnLocation(Location loc){
        for(ActiveEnvoy envoy : plugin.getEnvoyManager().getEnvoySpawner().getActiveEnvoys()){
            if(envoy.getSpawnLocations().contains(loc)){
                return envoy;
                }
            }
        return null;
    }

    }
