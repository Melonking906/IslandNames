package me.nonit.islandnames.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class FoundCommand implements CommandExecutor
{
    private static final String PREFIX = ChatColor.YELLOW + "[Loy]" + ChatColor.GREEN + " ";

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
    {
        sender.sendMessage( PREFIX + ChatColor.RED + "Please use " + ChatColor.WHITE + "/name" + ChatColor.RED + " to name islands now!" );
        return true;
    }
}