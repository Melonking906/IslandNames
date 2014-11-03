package me.nonit.islandnames.commands;

import com.github.hoqhuuep.islandcraft.api.ICBiome;
import com.github.hoqhuuep.islandcraft.api.ICIsland;
import com.github.hoqhuuep.islandcraft.api.ICWorld;
import com.github.hoqhuuep.islandcraft.api.IslandCraft;
import me.nonit.islandnames.IslandNames;
import me.nonit.islandnames.Oceans;
import me.nonit.islandnames.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class IslandCommand implements CommandExecutor
{
    private static final SQL db = IslandNames.getDb();
    private static final String PREFIX = ChatColor.YELLOW + "[Loy]" + ChatColor.GREEN + " ";
    private IslandCraft ic;
    private Oceans oc;

    public IslandCommand( IslandNames p )
    {
        this.ic = p.getIc();
        this.oc = new Oceans();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
    {
        if( ! (sender instanceof Player ) )
        {
            sender.sendMessage( PREFIX + "You must be a player to do that!" );
            return true;
        }

        Player p = (Player) sender;

        if( ! IslandNames.isIslandWorld( p.getLocation().getWorld().getName() ) )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry but.. there are no islands in this world." );
            return true;
        }

        ICWorld icWorld = ic.getWorld( p.getWorld().getName() );
        ICIsland icIsland = icWorld.getIslandAt( p.getLocation().getBlockX(), p.getLocation().getBlockZ() );

        String type = "Island";
        if( icWorld.getBiomeAt( icIsland.getCenter() ).equals( ICBiome.OCEAN ) )
        {
            type = "Sea";
        }
        else if( icWorld.getBiomeAt( icIsland.getCenter() ).equals( ICBiome.SWAMPLAND ) )
        {
            type = "Swamp";
        }

        String islandName = db.getName( icWorld.getName(), icIsland.getCenter() );
        String oceanName = oc.getOceanName( icIsland.getCenter() );

        if( islandName == null )
        {
            islandName = "Unnamed";
        }
        if( oceanName == null )
        {
            oceanName = ChatColor.MAGIC + "Unnamed";
        }

        p.sendMessage( PREFIX + "You're in " + ChatColor.YELLOW + islandName + " " + type + ChatColor.GREEN + ", of the " + ChatColor.YELLOW + oceanName + " Ocean" + ChatColor.GREEN + "!" );

        return true;
    }
}
