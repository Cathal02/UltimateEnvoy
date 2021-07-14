package org.cathal.ultimateEnvoy.utils;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import org.apache.commons.math3.util.Pair;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.scheduler.BukkitRunnable;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.envoys.ActiveEnvoy;
import org.cathal.ultimateEnvoy.envoys.events.CrateOpenEvent;
import org.cathal.ultimateEnvoy.envoys.events.EnvoyEndEvent;
import org.cathal.ultimateEnvoy.fileSystem.Language;
import org.cathal.ultimateEnvoy.gui.managers.envoys.EnvoyRefillMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HologramManager implements Listener{
    private UltimateEnvoy plugin;
    private boolean useHolographicDisplays;

    private Map<Hologram,Location> perCrateHologramUpdateLocations = new HashMap<>();
    private Map<ActiveEnvoy,List<Pair<Hologram,Location>>> envoyRefillHologramUpdateLocations = new HashMap<>();


    public HologramManager(UltimateEnvoy ultimateEnvoy, boolean useHolographicDisplays) {
        plugin = ultimateEnvoy;
        this.useHolographicDisplays = useHolographicDisplays;
        Logger.getLogger("minecraft").log(Level.WARNING, "[UltimateEnvoy] Holographic Displays not enabled.");
        ultimateEnvoy.getServer().getPluginManager().registerEvents(this,plugin);

        refillTimerUpdater();

    }

    public void createHologram(Location location,String text){
        if(!useHolographicDisplays) return;
        Hologram hologram = HologramsAPI.createHologram(plugin, getHologramLocation(location));
        hologram.appendTextLine(ChatColor.translateAlternateColorCodes('&',text));
    }

    public void createHologram(Location location, List<String> text){
        if(!useHolographicDisplays) return;
    }

    public Location getHologramLocation(Location blockLocation)
    {
        return new Location(blockLocation.getWorld(),blockLocation.getBlockX()+0.5,blockLocation.getBlockY()+2,blockLocation.getBlockZ()+0.5);
    }

    public Location getChestLocation(Location hologramLocation){
        return new Location(hologramLocation.getWorld(),hologramLocation.getBlockX()-0.5,hologramLocation.getBlockY()-2,hologramLocation.getBlockZ()-0.5);
    }

    public Hologram getHologram(Location location){
        Location loc = getHologramLocation(location);
        for(Hologram hologram : HologramsAPI.getHolograms(plugin)){
            if(hologram.getLocation().equals(loc)) {
                return hologram;
            }
        }

        return null;
    }

    public void removeHologram(Location loc) {
        if(!useHolographicDisplays)return;
        for(Hologram hologram : HologramsAPI.getHolograms(plugin)){
            if(hologram.getLocation().equals(getHologramLocation(loc))){
                hologram.delete();
            }
        }
    }

    public void refillTimerUpdater(){
        new BukkitRunnable(){
            private Map<Hologram, Integer> lastIndividualCrateUpdate = new HashMap<>();
            @Override
            public void run() {
                if(perCrateHologramUpdateLocations.keySet().size() < 1)return;
                List<Hologram> removeHolograms = new ArrayList<>();

                for(Hologram hologram : perCrateHologramUpdateLocations.keySet()){


                    // If crate is not active i.e. should have a hologram
                    Location loc = perCrateHologramUpdateLocations.get(hologram);

                    if(!plugin.getEnvoyManager().getCrateManager().getCrateSpawner().isActiveCratePosition(loc)){

                        if(!lastIndividualCrateUpdate.containsKey(hologram)){
                            lastIndividualCrateUpdate.put(hologram,4);
                        } else if(lastIndividualCrateUpdate.get(hologram) !=0 ){
                            lastIndividualCrateUpdate.put(hologram, lastIndividualCrateUpdate.get(hologram)-1);
                            continue;
                        } else {
                            lastIndividualCrateUpdate.put(hologram,4);
                        }


                        if(hologram.size()>1){
                            hologram.removeLine(1);
                        }

                        long timeLeft = plugin.getEnvoyManager().getEnvoyRefiller().getTimeToCrateRefill(loc);
                        hologram.appendTextLine(Language.getString("crateRefillHologram",true).replaceAll("%duration%",String.valueOf(timeLeft)));
                    }else{
                        //Remove line
                        hologram.removeLine(hologram.size()-1);
                        removeHolograms.add(hologram);
                    }
                }

                if(removeHolograms.size() > 0){
                    perCrateHologramUpdateLocations.keySet().removeAll(removeHolograms);
                    removeHolograms.clear();
                }
            }
        }.runTaskTimer(plugin,0,20L );

        updateEnvoyHolograms();
    }

    private void updateEnvoyHolograms(){

        new BukkitRunnable(){
            Map<ActiveEnvoy,List<Pair<Hologram,Location>>> removeHolograms = new HashMap<>();

            @Override
            public void run() {
                if(envoyRefillHologramUpdateLocations.keySet().size() < 1)return;

                for(ActiveEnvoy envoy : envoyRefillHologramUpdateLocations.keySet()){
                    for(Pair<Hologram,Location> hologramPair : envoyRefillHologramUpdateLocations.get(envoy)){
                        // If crate is not active i.e. should have a hologram
                        if(!plugin.getEnvoyManager().getCrateManager().getCrateSpawner().isActiveCratePosition(hologramPair.getValue())){

                            if(hologramPair.getKey().size()> envoy.getEnvoy().getBaseHologramLength()){
                                hologramPair.getKey().removeLine(hologramPair.getKey().size()-1);
                            }

                            long timeLeft = plugin.getEnvoyManager().getEnvoyRefiller().getTimeToEnvoyRefill(envoy);
                            hologramPair.getKey().appendTextLine(Language.getString("crateRefillHologram",true).replaceAll("%duration%",String.valueOf(timeLeft)));
                        }else{
                            //Remove line
                            if(hologramPair.getKey().size() > envoy.getEnvoy().getBaseHologramLength()){
                                hologramPair.getKey().removeLine(hologramPair.getKey().size()-1);
                            }

                            if(!removeHolograms.containsKey(envoy)) {
                                removeHolograms.put(envoy, new ArrayList<>());
                            }
                            removeHolograms.get(envoy).add(hologramPair);
                        }
                    }
                }

                if(removeHolograms.size() > 0){
                    for(ActiveEnvoy envoy : removeHolograms.keySet()){
                        envoyRefillHologramUpdateLocations.get(envoy).removeAll(removeHolograms.get(envoy));
                    }
                    removeHolograms.clear();
                }
            }




    }.runTaskTimer(plugin,0L,60L);
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void handleCrateOpen(CrateOpenEvent e){
        // Start polling.
        if(!e.getActiveEnvoy().getEnvoy().displayRefillTimer())return;

        if(e.getActiveEnvoy().getEnvoy().getRefillMode() == EnvoyRefillMode.PER_CRATE){
            perCrateHologramUpdateLocations.put(getHologram(e.getLocation()),e.getLocation());
        }else {
            if(!envoyRefillHologramUpdateLocations.containsKey(e.getActiveEnvoy())){
                envoyRefillHologramUpdateLocations.put(e.getActiveEnvoy(),new ArrayList<>());
            }

            envoyRefillHologramUpdateLocations.get(e.getActiveEnvoy()).add(new Pair<>(getHologram(e.getLocation()),e.getLocation()));

        }
    }


    @EventHandler
    public void onEnvoyEnd(EnvoyEndEvent e){
        envoyRefillHologramUpdateLocations.remove(e.getActiveEnvoy());
    }
}
