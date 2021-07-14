package org.cathal.ultimateEnvoy.fileSystem;

import net.milkbowl.vault.chat.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.envoys.Envoy;

import java.io.File;
import java.io.IOException;

public class Language {
    private File customConfigFile;
    private static FileConfiguration customConfig;
    private static UltimateEnvoy plugin;

    public Language(UltimateEnvoy _plugin){
        plugin = _plugin;
        createCustomConfig();
    }

    public static void broadcast(String message) {
        Bukkit.broadcastMessage(message);
    }

    public static String getEnvoyRefillMessage(Envoy envoy) {
        return getString("envoyRefill",true).replaceAll("%envoyName%",envoy.getName());

    }


    public FileConfiguration getCustomConfig() {
        return customConfig;
    }

    public static String getString(String message, boolean translate){
        String s = customConfig.getString(message);
        if(s == null){
            return ChatColor.RED + "Message not found";
        }

        return translate ? ChatColor.translateAlternateColorCodes('&', s) : s;
    }

    public static Configuration getConfig(){
        return plugin.getConfig();
    }

    public static String getConfigString(String message, boolean translate){
        String s = customConfig.getString(message);
        if(translate && s != null){
            return ChatColor.translateAlternateColorCodes('&', s);
        }

        return s;
    }

    public static String translate(String message){
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    private void createCustomConfig() {
        customConfigFile = new File(plugin.getDataFolder().getPath() + File.separator + "lang.yml");
        if (!customConfigFile.exists()) plugin.saveResource(customConfigFile.getName(), false);


        customConfig= new YamlConfiguration();
        try {
            customConfig.load(customConfigFile);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }


    public static String translateBalanceMessage(String message, double balance){
        message = message.replaceAll("%balance%", String.valueOf(balance));
        return translate(message);
    }

    public static String translateEnvoyMessage(String sentence,String envoyName, int duration){
        if(envoyName != null){
            sentence = sentence.replaceAll("%envoyName%", translate(envoyName));
        }

        if(duration != 0){
            sentence = sentence.replaceAll("%duration%", Integer.toString(duration));
        }

        return translate(sentence);
    }

    public static String getNotEnoughPlayersOnline(int amount, String envoyName){
        String message = getString("notEnoughPlayersOnline",false);
        message = message.replaceAll("%amount%", String.valueOf(amount));
        if(envoyName != null){
            message = message.replaceAll("%envoyName%", envoyName);
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public static String getEnvoyOverMessage(String name) {
        String message = getString("envoyOverBroadcast",false);
        message = message.replaceAll("%envoyName%",name);

        return ChatColor.translateAlternateColorCodes('&',message);
    }
}
