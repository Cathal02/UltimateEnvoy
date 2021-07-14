package org.cathal.ultimateEnvoy.gui.managers.envoys;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.ActiveEnvoy;
import org.bukkit.event.Listener;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.events.CrateOpenEvent;
import org.cathal.ultimateEnvoy.fileSystem.Language;
import org.cathal.ultimateEnvoy.envoys.events.EnvoyEndEvent;
import org.cathal.ultimateEnvoy.envoys.events.EnvoyStartEvent;
import org.cathal.ultimateEnvoy.utils.Settings;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnvoyRefiller implements Listener{

    UltimateEnvoy plugin;
    private Map<ActiveEnvoy, List<BukkitTask>> taskMap = new HashMap<>();

    private Map<Location, LocalDateTime> crateTimerFinishTimeStamp = new HashMap<>();
    private Map<ActiveEnvoy, LocalDateTime> envoyTimerFinishTimeStamp = new HashMap<>();

    public EnvoyRefiller(UltimateEnvoy plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }



    public void refillEnvoy(ActiveEnvoy activeEnvoy){
        envoyTimerFinishTimeStamp.put(activeEnvoy,LocalDateTime.now().plusSeconds(activeEnvoy.getEnvoy().getRefillTimer()));

        if(activeEnvoy.getEnvoy().getRefillMode() == EnvoyRefillMode.ALL_CRATES){
            BukkitTask runnable = new BukkitRunnable(){
                @Override
                public void run() {
                    plugin.getEnvoyManager().getCrateManager().getCrateSpawner().resetCratesOpenedStatus();
                    if(Settings.announceEnvoyRefill()){
                        Language.broadcast(Language.getEnvoyRefillMessage(activeEnvoy.getEnvoy()));
                    }
                    envoyTimerFinishTimeStamp.put(activeEnvoy,LocalDateTime.now().plusSeconds(activeEnvoy.getEnvoy().getRefillTimer()));

                }
            }.runTaskTimer(plugin,activeEnvoy.getEnvoy().getRefillTimer()*20,activeEnvoy.getEnvoy().getRefillTimer()*20);

            addToTaskMap(activeEnvoy,runnable);
        }
    }


    // Used when a crate is opened individually
    public void startRefillCrateTimer(Crate crate, ActiveEnvoy activeEnvoy, Location loc){

        //Inserted to allow for tracking of time until crate reset
        crateTimerFinishTimeStamp.put(loc, LocalDateTime.now().plusSeconds(activeEnvoy.getEnvoy().getRefillTimer()));
        BukkitTask runnable = new BukkitRunnable(){
            @Override
            public void run() {
                plugin.getEnvoyManager().getCrateManager().getCrateSpawner().resetCrateOpenedStatus(crate,loc);

                // Remove tracking of timestamp
                crateTimerFinishTimeStamp.remove(loc);
            }
        }.runTaskLater(plugin,activeEnvoy.getEnvoy().getRefillTimer()*20);

        addToTaskMap(activeEnvoy,runnable);
    }

    public void addToTaskMap(ActiveEnvoy envoy, BukkitTask task){
        if(taskMap.containsKey(envoy)){
            taskMap.get(envoy).add(task);
        }else {
            ArrayList<BukkitTask> tasks = new ArrayList<>();
            tasks.add(task);

            taskMap.put(envoy,tasks);
        }
    }



    public Long getTimeToCrateRefill(Location loc){
        if(!crateTimerFinishTimeStamp.containsKey(loc))return -1L;

       return ChronoUnit.SECONDS.between(LocalDateTime.now(),crateTimerFinishTimeStamp.get(loc))+1;
    }

    public Long getTimeToEnvoyRefill(ActiveEnvoy envoy){
        if(!envoyTimerFinishTimeStamp.containsKey(envoy))return -1L;
        return ChronoUnit.SECONDS.between(LocalDateTime.now(),envoyTimerFinishTimeStamp.get(envoy))+1;
    }


    @EventHandler
    public void onCrateOpen(CrateOpenEvent e){
        // Find all active envoys
        for(ActiveEnvoy envoy : plugin.getEnvoyManager().getEnvoySpawner().getActiveEnvoys()){
            if(envoy.getSpawnLocations().contains(e.getLocation())){
                if(envoy.getEnvoy().getRefillMode() == EnvoyRefillMode.PER_CRATE){
                    startRefillCrateTimer(e.getCrate(),envoy,e.getLocation());
                }
            }
        }
    }

    @EventHandler
    public void onEnvoyStart(EnvoyStartEvent e){
        refillEnvoy(e.getActiveEnvoy());
    }
    @EventHandler
    public void onEnvoyEnd(EnvoyEndEvent e){
        if(taskMap.size()<1)return;

        taskMap.get(e.getActiveEnvoy()).forEach(BukkitTask::cancel);
    }

}
