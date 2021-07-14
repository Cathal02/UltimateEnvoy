package org.cathal.ultimateEnvoy.envoys;

import org.bukkit.Location;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.utils.TimeUtils;

import java.util.ArrayList;
import java.util.List;

public class ActiveEnvoy {
    private Envoy envoy;
    private List<Location> spawnLocations = new ArrayList<>();
    private UltimateEnvoy plugin;

    public ActiveEnvoy(UltimateEnvoy plugin, Envoy envoy){
        this.plugin = plugin;
        this.envoy = envoy;

    }


    public void addSpawnLocation(Location loc) {
        spawnLocations.add(loc);
    }

    public List<Location> getSpawnLocations() {
        return spawnLocations;
    }

    public Envoy getEnvoy(){
        return envoy;
    }

    public EnvoyDate getSpawnedDate(){return TimeUtils.getCurrentDate(envoy);}
}
