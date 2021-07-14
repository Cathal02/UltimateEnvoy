package org.cathal.ultimateEnvoy.envoys;

import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.envoys.crates.CrateManager;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.fileSystem.EnvoyDataManager;
import org.cathal.ultimateEnvoy.gui.managers.envoys.EnvoyRefiller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class EnvoyManager {

    private List<Envoy> envoys = new ArrayList<>();
    private final UltimateEnvoy plugin;
    private final CrateManager crateManager;
    private final EnvoyDataManager envoyDataManager;
    private final EnvoyEditorManager envoyEditorManager;
    private final EnvoySpawner envoySpawner;
    private final EnvoyScheduler envoyScheduler;
    private final EnvoyRefiller envoyRefiller;

    public EnvoyManager(UltimateEnvoy ultimateEnvoy) {
        this.plugin = ultimateEnvoy;
        envoyDataManager = new EnvoyDataManager(ultimateEnvoy, this);
        crateManager = new CrateManager(ultimateEnvoy);
        envoyEditorManager = new EnvoyEditorManager(ultimateEnvoy);
        envoyRefiller = new EnvoyRefiller(ultimateEnvoy);
        envoySpawner = new EnvoySpawner(ultimateEnvoy,envoyRefiller);
        this.envoys = envoyDataManager.getEnvoyData();

        envoyScheduler = new EnvoyScheduler(ultimateEnvoy,this);
    }

    public void createEnvoy(String name) {
        Envoy envoy = new Envoy(name, getNextId());
        envoys.add(envoy);
    }

    public int getNextId(){
        if(envoys.size() < 1){
            return 0;
        }
        return envoys.stream().sorted(Comparator.comparing(Envoy::getId).reversed()).collect(Collectors.toList()).get(0).getId()+1;
    }

    public void save() {
        envoyDataManager.saveEnvoys(envoys);
    }

    public EnvoyEditorManager getEnvoyEditorManager(){return envoyEditorManager;}
    public CrateManager getCrateManager(){
        return  crateManager;
    }
    public EnvoyRefiller getEnvoyRefiller(){return envoyRefiller;}
    public Envoy[] getAllEnvoys(){
        return envoys.toArray(new Envoy[envoys.size()]);
    }
    public EnvoySpawner getEnvoySpawner(){return envoySpawner;}
    public Envoy getEnvoyByID(Integer i) {

        return envoys.stream().filter(c -> c.getId()==i).findFirst().orElse(null);

    }

    public void deleteEnvoy(Envoy envoy) {
        envoys.remove(envoy);
    }
}
