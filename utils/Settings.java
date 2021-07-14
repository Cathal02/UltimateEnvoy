package org.cathal.ultimateEnvoy.utils;

import org.cathal.ultimateEnvoy.UltimateEnvoy;

public class Settings {

    private static UltimateEnvoy plugin;
    public Settings(UltimateEnvoy _plugin){
        plugin = _plugin;
    }

    public static boolean announceEnvoyIfNotEnoughPlayersOnline(){
        return plugin.getConfig().getBoolean("announceEnvoyIfNotEnoughPlayersOnline");
    }

    public static boolean announceEnvoyRefill() {
        return plugin.getConfig().getBoolean("announceEnvoyRefill");
    }
}
