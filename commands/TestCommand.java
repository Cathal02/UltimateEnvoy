package org.cathal.ultimateEnvoy.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalField;

public class TestCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        LocalDateTime timePoint = LocalDateTime.now();
        DayOfWeek day = timePoint.getDayOfWeek();
        return true;
    }
}
