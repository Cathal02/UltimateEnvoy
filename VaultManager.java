package org.cathal.ultimateEnvoy;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.util.logging.Logger;

public class VaultManager {
    private static final Logger log = Logger.getLogger("Minecraft");
    private static Economy econ = null;

    public VaultManager(UltimateEnvoy plugin){
        if (!setupEconomy() ) {
            log.severe(String.format("UltimateEnvoy - Disabled due to no Vault dependency found!"));
            Bukkit.getServer().getPluginManager().disablePlugin(plugin);
        }
    }
    
    private boolean setupEconomy() {
        if (Bukkit.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }
}
