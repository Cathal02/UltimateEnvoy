package org.cathal.ultimateEnvoy.envoys.crates;

import org.bukkit.Material;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.util.*;
import java.util.stream.Collectors;

public class CrateManager {
    private UltimateEnvoy plugin;

    private List<Crate> crates = new ArrayList<>();
    private final CrateSpawner crateSpawner;
    private final CrateOpener crateOpener;

    public CrateManager(UltimateEnvoy ultimateEnvoy) {
        this.plugin = ultimateEnvoy;
        crateSpawner = new CrateSpawner(ultimateEnvoy);
        crateOpener = new CrateOpener(ultimateEnvoy);

        initCrates();

    }

    private void initCrates() {
        crates = plugin.getCrateDataManager().getCrateData();
        if (crates == null) {
            crates = new ArrayList<>();
        }
    }

    public Crate[] getAllCrates() {
        if (crates.size() < 1) return new Crate[0];

        return crates.toArray(new Crate[crates.size()]);
    }

    public List<Crate> getAllCratesAsList() {
        return crates;
    }


    public void createCrate(String crateName) {
        Crate newCrate = new Crate(crateName, getNextId());
        crates.add(newCrate);
    }

    private int getNextId(){
        if(crates.size() < 1){
            return 0;
        }
        return crates.stream().sorted(Comparator.comparing(Crate::getId).reversed()).collect(Collectors.toList()).get(0).getId()+1;
    }

    public void editCrateName(Crate crate, String text) {
        crate.setName(text);
    }

    public void cycleCrateOpenMethod(Crate crate) {
        switch (crate.getCrateOpenMethod()) {
            case INSERT_TO_INVENTORY:
                crate.setOpenMethod(CrateOpenMethod.DROP_ON_FLOOR);
                break;

            case DROP_ON_FLOOR:
                crate.setOpenMethod(CrateOpenMethod.OPEN_CHEST);
                break;

            case OPEN_CHEST:
            default:
                crate.setOpenMethod(CrateOpenMethod.INSERT_TO_INVENTORY);
                break;

        }
    }

    public void updateCrateItem(Crate crate, Material newItem) {
        crate.setCrateItem(newItem);
    }

    public Crate getCrateByID(Integer i){
        return crates.stream().filter(c -> c.getId()==i).findFirst().orElse(null);
    }
    public CrateSpawner getCrateSpawner(){
            return crateSpawner;
        }
    public CrateOpener getCrateOpener() {
        return crateOpener;
    }

    public List<Crate> convertIdsToCrates(Collection<Integer> ids) {
        return ids.stream().map(this::getCrateByID).collect(Collectors.toList());
    }

    public void setCratePermission(Crate crate, String text) {
        crate.setRequiredPermission(text);
    }

    public void setCrateBalance(Crate crate, double balance) {
        crate.setBalanceRequired(balance);
    }
}
