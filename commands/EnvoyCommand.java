package org.cathal.ultimateEnvoy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cathal.ultimateEnvoy.UltimateEnvoy;
import org.cathal.ultimateEnvoy.fileSystem.Language;

public class EnvoyCommand implements CommandExecutor {

    private final UltimateEnvoy plugin;
    public EnvoyCommand(UltimateEnvoy plugin){
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender.hasPermission("ultimateenvoy.admin") || sender.hasPermission("ultimateenvoy.*") || sender.isOp()){
            if(!(sender instanceof Player)){
                sender.sendMessage(Language.getString("mustBePlayer", true));
                return true;
            }
            plugin.getEnvoyGuiManager().open((Player)sender);
            return true;
        }else
        {
            sender.sendMessage(Language.getString("nextEnvoy", true).replaceAll("{time}", "10"));
        }
        return true;
    }
}
