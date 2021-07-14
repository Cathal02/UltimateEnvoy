package org.cathal.ultimateEnvoy.envoys;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.scheduler.BukkitRunnable;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.fileSystem.Language;
import org.cathal.ultimateEnvoy.envoys.events.EnvoyEndEvent;
import org.cathal.ultimateEnvoy.utils.Settings;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.event.Listener;

public class EnvoyScheduler implements Listener {
    private final EnvoyManager envoyManager;
    private final UltimateEnvoy plugin;

    private Map<Integer, List<EnvoyDate>> spawnAttempts = new HashMap<>();

    public EnvoyScheduler(UltimateEnvoy plugin, EnvoyManager envoyManager) {
        this.envoyManager = envoyManager;
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        setupTask();

    }

    //TODO: Doesn't support time zones N.B.
    private void setupTask() {

        new BukkitRunnable() {
            LocalDateTime time;

            @Override
            public void run() {
                time = LocalDateTime.now();
                for (Envoy envoy : envoyManager.getAllEnvoys()) {
                    for (EnvoyDate date : envoy.getEnvoyDates()) {
                        if (spawnAttempts.containsKey(envoy.getId()) && spawnAttempts.get(envoy.getId()).contains(date))
                            continue;

                        if (date.checkDate(time.getDayOfWeek().getValue() - 1, time.getHour(), time.getMinute()) &&time.getSecond()==0) {
                            if (envoyCanSpawn(envoy)) {
                                envoyManager.getEnvoySpawner().spawnEnvoy(envoy);
                            }
                            if (!spawnAttempts.containsKey(envoy.getId())) {
                                spawnAttempts.put(envoy.getId(), new ArrayList<>());
                            }
                            spawnAttempts.get(envoy.getId()).add(date);
                        }
                    }
                }
            }

            private boolean envoyCanSpawn(Envoy envoy) {

                if (Bukkit.getServer().getOnlinePlayers().size() < envoy.getPlayersRequiredToStartEnvoy()) {
                    if (Settings.announceEnvoyIfNotEnoughPlayersOnline()) {
                        Language.broadcast(Language.getNotEnoughPlayersOnline(envoy.getPlayersRequiredToStartEnvoy(), envoy.getName()));
                    }
                    return false;
                }

                for (ActiveEnvoy activeEnvoy : plugin.getEnvoyManager().getEnvoySpawner().getActiveEnvoys()) {
                    if (activeEnvoy.getEnvoy().getId() == envoy.getId()) {
                        Logger.getLogger("minecraft").log(Level.WARNING, "[UltimateEnvoy] Trying to spawn envoy " + envoy.getName() + ". It is already in progress!");
                        return false;
                    }
                }
                return true;
            }
        }.runTaskTimer(plugin, 0, 20L);
    }

    @EventHandler
    public void onEventEnd(EnvoyEndEvent e){
        List<EnvoyDate> dates = spawnAttempts.get(e.getActiveEnvoy().getEnvoy().getId());
        if(dates != null){
            dates.remove(e.getActiveEnvoy().getSpawnedDate());
        }
    }
}


