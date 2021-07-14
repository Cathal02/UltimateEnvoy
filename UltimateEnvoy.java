package org.cathal.ultimateEnvoy;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.cathal.ultimateEnvoy.commands.EnvoyCommand;
import org.cathal.ultimateEnvoy.commands.SpawnCommand;
import org.cathal.ultimateEnvoy.commands.TestCommand;
import org.cathal.ultimateEnvoy.envoys.EnvoyManager;
import org.cathal.ultimateEnvoy.envoys.crates.RewardManager;
import org.cathal.ultimateEnvoy.fileSystem.CrateDataManager;
import org.cathal.ultimateEnvoy.fileSystem.Language;
import org.cathal.ultimateEnvoy.fileSystem.RewardDataManager;
import org.cathal.ultimateEnvoy.gui.InventoryNavigator;
import org.cathal.ultimateEnvoy.gui.listeners.CrateFallListener;
import org.cathal.ultimateEnvoy.gui.listeners.CrateInteractListener;
import org.cathal.ultimateEnvoy.gui.listeners.EnvoyEditListener;
import org.cathal.ultimateEnvoy.gui.managers.EnvoyGuiManager;
import org.cathal.ultimateEnvoy.gui.listeners.InventoryClickListener;
import org.cathal.ultimateEnvoy.utils.BlockPlacer;
import org.cathal.ultimateEnvoy.utils.HologramManager;
import org.cathal.ultimateEnvoy.utils.Settings;

public final class UltimateEnvoy extends JavaPlugin {


    private EnvoyGuiManager envoyGuiManager;
    private EnvoyManager envoyManager;
    private CrateDataManager crateDataManager;
    private RewardDataManager rewardDataManager;
    private RewardManager rewardManager;
    private HologramManager hologramManager;
    private InventoryNavigator inventoryNavigator;
    private ProtocolManager protocolManager;
    private  VaultManager vaultManager;

    public void onLoad() {
        protocolManager = ProtocolLibrary.getProtocolManager();
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        saveDefaultConfig();
        new BlockPlacer(this);
        new Language(this);
        new Settings(this);

        registerCommands();
            boolean useHolographicDisplays = Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays");
            vaultManager = new VaultManager(this);
            hologramManager = new HologramManager(this, useHolographicDisplays);
            rewardDataManager = new RewardDataManager(this);
            rewardManager = new RewardManager(this);

            crateDataManager = new CrateDataManager(this);
            envoyGuiManager = new EnvoyGuiManager(this);
            envoyManager = new EnvoyManager(this);

            inventoryNavigator = new InventoryNavigator(this);

            new EnvoyEditListener(this);
            new CrateFallListener(this);
            new CrateInteractListener(this);
            new InventoryClickListener(this);

    }

    private void registerCommands() {
        getCommand("envoy").setExecutor(new EnvoyCommand(this));
        getCommand("spawn").setExecutor(new SpawnCommand(this));
        getCommand("test").setExecutor(new TestCommand());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getEnvoyManager().getEnvoyEditorManager().serverClose();
        crateDataManager.saveCrates(envoyManager.getCrateManager().getAllCratesAsList());
        rewardDataManager.saveCrateRewards(rewardManager.getRewards());
        envoyManager.save();
        getEnvoyManager().getEnvoySpawner().cleanupAllEnvoys();

    }

    public EnvoyGuiManager getEnvoyGuiManager(){
        return envoyGuiManager;
    }
    public EnvoyManager getEnvoyManager(){
        return envoyManager;
    }
    public CrateDataManager getCrateDataManager() {
        return crateDataManager;
    }
    public RewardManager getRewardManager() {
        return rewardManager;
    }
    public RewardDataManager getRewardDataManager() {
        return rewardDataManager;
    }
    public HologramManager getHologramManager(){
        return hologramManager;
    }
    public InventoryNavigator getInventoryNavigator(){return inventoryNavigator;}
    public ProtocolManager getPacketManager(){return protocolManager;}
}
