package org.cathal.ultimateEnvoy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cathal.ultimateEnvoy.UltimateEnvoy;

import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class SpawnCommand implements CommandExecutor {
    UltimateEnvoy plugin;
    public SpawnCommand(UltimateEnvoy ultimateEnvoy) {
            this.plugin = ultimateEnvoy;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        plugin.getEnvoyManager().getEnvoySpawner().spawnEnvoy(plugin.getEnvoyManager().getEnvoyByID(0));
        return true;
    }
}
