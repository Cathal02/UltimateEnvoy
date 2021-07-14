package org.cathal.ultimateEnvoy.envoys;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;
import org.bukkit.Location;
import org.cathal.ultimateEnvoy.envoys.crates.Crate;
import org.cathal.ultimateEnvoy.gui.managers.envoys.EnvoyRefillMode;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Envoy {
    private String envoyName;
    private Map<Crate, Double> crates = new HashMap<>();
    private EnumeratedDistribution crateDistribution;
    private List<Location> crateSpawnLocations = new ArrayList<>();
    private List<EnvoyDate> envoyDates = new ArrayList<>();

    private boolean randomCratePosition = true;
    private boolean showRefillTimer = true;
    private boolean enableFallingCrates = true;
    private int minCrates = 2;
    private int maxCrates = 3;
    private int playersRequiredToStartEnvoy = 5;
    private int envoyDuration;
    private int refillTimer = 30;
    private EnvoyRefillMode envoyRefillMode = EnvoyRefillMode.PER_CRATE;
    private Location edgeOne;
    private Location edgeTwo;

    private int id;

    public Envoy(String envoyName, int id){
        this.envoyName = envoyName;
        this.id = id;

        updateDistribution();

    }

    public Envoy(EnvoyManager envoyManager,String envoyName, int id, Location edgeOne, Location edgeTwo,
                 Map<Integer, Double> crateInput, boolean randomCratePosition, List<Location> crateSpawnLocations, List<EnvoyDate> envoyDates,
                 int playersRequiredToStartEnvoy, int envoyDuration, int minCrates, int maxCrates, boolean enableFallingCrates, EnvoyRefillMode refillMode){
        this.envoyName = envoyName;
        this.id = id;
        this.edgeOne = edgeOne;
        this.edgeTwo = edgeTwo;
        this.randomCratePosition = randomCratePosition;
        this.crateSpawnLocations = crateSpawnLocations;
        this.envoyDates = envoyDates;
        this.playersRequiredToStartEnvoy = playersRequiredToStartEnvoy;
        this.enableFallingCrates = enableFallingCrates;
        this.envoyRefillMode = refillMode;
        this.envoyDuration = envoyDuration;

        this.minCrates = minCrates;
        this.maxCrates = maxCrates;
        // Converts map of Map<CrateId, CrateChance> to map of <Crate, Chance>
        if(crateInput != null){
            List<Crate> idsToCrates = envoyManager.getCrateManager().convertIdsToCrates(crateInput.keySet());
            Iterator<Crate> crateIterator = idsToCrates.iterator();
            Iterator<Double> chanceIterator = crateInput.values().iterator();
            while(crateIterator.hasNext() && chanceIterator.hasNext()){
                crates.put(crateIterator.next(), chanceIterator.next());
            }
        }

        updateDistribution();
    }

    public String getName() {
        return envoyName;
    }

    public void setEdgeOne(Location loc) {
        edgeOne = loc;
    }

    public void setEdgeTwo(Location loc) {
        edgeTwo = loc;
    }

    public int getId(){
        return id;
    }

    public int getPlayersRequiredToStartEnvoy(){return playersRequiredToStartEnvoy;}

    public Location getEdgeOne(){
        return edgeOne;
    }

    public Crate[] getCratesToSpawn(){
        if(getCrates().size() ==0)return new Crate[0];
        if(crateSpawnLocations.size() == 0 && !getIsEnvoyUsingRandomCrateSpawnPositions()){
            Logger.getLogger("minecraft").log(Level.WARNING, "[UltimateEnvoy] trying to spawn crates using defined positions. NONE SET. ");
            return new Crate[0];
        }


        int amountToSpawn = 0;
        // locs 20
        // min 15
        // max 40
        if(!getIsEnvoyUsingRandomCrateSpawnPositions() && maxCrates > crateSpawnLocations.size()){
            if(minCrates > crateSpawnLocations.size()){
                amountToSpawn = crateSpawnLocations.size();
            } else {
                amountToSpawn = minCrates + ThreadLocalRandom.current().nextInt(crateSpawnLocations.size()-minCrates);
            }
            Logger.getLogger("minecraft").log(Level.WARNING, "[UltimateEnvoy] Trying to spawn more crates than spawn positions set. Limiting crate spawns. ");


        } else {
            amountToSpawn = minCrates + ThreadLocalRandom.current().nextInt(maxCrates+1);
        }

        Crate[] crates = new Crate[amountToSpawn];
        crateDistribution.sample(amountToSpawn, crates);
        return crates;
    }

    public List<Crate> getCrates(){
        return new ArrayList<Crate>(crates.keySet());
    }

    public Map<Integer, Double> getCrateMap(){
        Map<Integer, Double> map = new HashMap<>();
        for(Crate crate : crates.keySet()){
            map.put(crate.getId(), crates.get(crate));
        }

        return map;
    }
    public void addCrate(Crate crate){
            crates.put(crate, 10d);
            updateDistribution();
    }

    public void addCrate(Crate crate, double odds){
        crates.put(crate,odds);
        updateDistribution();
    }

    public void removeCrate(Crate crate){
        crates.remove(crate);
    }

    private void updateDistribution() {
        if(crates.keySet().size() ==0)return;
        final List<Pair<Crate, Double>> itemWeights = new ArrayList<>();
        for(Crate crate : crates.keySet()){
            itemWeights.add(new Pair(crate,crates.get(crate)));
        }

        crateDistribution = new EnumeratedDistribution(itemWeights);
    }

    public Location getEdgeTwo(){
        return edgeTwo;
    }


    public boolean getIsEnvoyUsingRandomCrateSpawnPositions() {

        return randomCratePosition;
    }

    public void toggleCrateRandomSpawn() {
        randomCratePosition = !randomCratePosition;
    }

    public List<Location> getCrateSpawnLocations(){
        return crateSpawnLocations;
    }

    public void addCrateSpawnLocation(Location location) {
        crateSpawnLocations.add(location);
    }

    public void removeCrateSpawnLocation(Location loc) {
        crateSpawnLocations.remove(loc);
    }

    public List<EnvoyDate> getEnvoyDates(){return envoyDates;}

    public void addDate(EnvoyDate date) {

        //TODO: Check duplicates
        envoyDates.add(date);
    }

    public void removeDate(EnvoyDate envoyDate) {
        envoyDates.remove(envoyDate);
    }

    public void increasePlayersRequiredToStart(int amount){
        playersRequiredToStartEnvoy += amount;
    }

    public void decreasePlayersRequiredToStart(int amount){
        playersRequiredToStartEnvoy -= amount;
        if(playersRequiredToStartEnvoy<0){
            playersRequiredToStartEnvoy=0;
        }
    }

    public int getEnvoyDuration(){
        return envoyDuration;
    }

    public void updateDuration(int duration) {
        envoyDuration = Math.max(duration, 60);;
    }

    public int getRefillTimer(){return refillTimer;}
    public double getCrateProbability(Crate crate){
        return crates.get(crate);
    }

    public void updateCrateProbability(Crate crate, double probability) {
        crates.put(crate,probability);
        updateDistribution();
    }


    public void updateEnvoyTimer(int timer) {
        refillTimer = Math.max(timer, 0);
    }

    public void setMinCrates(int amount){
        minCrates = Math.max(amount,0);
        if(minCrates > maxCrates){
            maxCrates = minCrates;
        }
    }

    public void setMaxCrates(int amount){
        maxCrates = Math.max(amount,0);
        if(maxCrates<minCrates){
            minCrates = maxCrates;
        }
    }

    public int getMinCrates(){return minCrates;}
    public int getMaxCrates(){return maxCrates;}

    public boolean isEnableFallingCrates() {
        return enableFallingCrates;
    }

    public void toggleFallingCrates() {
        this.enableFallingCrates = !this.enableFallingCrates;
    }

    public EnvoyRefillMode getRefillMode() {
        return envoyRefillMode;

    }

    public void toggleRefillMode() {
        if(envoyRefillMode == EnvoyRefillMode.ALL_CRATES){
            envoyRefillMode = EnvoyRefillMode.PER_CRATE;
        }else {
            envoyRefillMode = EnvoyRefillMode.ALL_CRATES;
        }
    }

    public boolean getShowRefillTimer() {
        return showRefillTimer;
    }


    //TODO: Implement option.
    public boolean displayRefillTimer() {
        return true;
    }

    public int getBaseHologramLength(){return 1;}
}

