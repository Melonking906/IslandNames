package me.nonit.islandnames;

import com.github.hoqhuuep.islandcraft.api.ICBiome;
import com.github.hoqhuuep.islandcraft.api.ICIsland;
import com.github.hoqhuuep.islandcraft.api.ICWorld;
import com.github.hoqhuuep.islandcraft.api.IslandCraft;
import me.nonit.islandnames.databases.SQL;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerListener implements Listener
{
    private IslandCraft ic;
    private static final SQL db = IslandNames.getDb();

    public PlayerListener( IslandNames p )
    {
        ic = p.getIc();
    }

    @EventHandler
    public void onMove( PlayerMoveEvent e )
    {
        Player p = e.getPlayer();

        if( ! IslandNames.isIslandWorld( p.getLocation().getWorld().getName() ) )
        {
            return;
        }

        ICWorld icWorld = ic.getWorld( e.getPlayer().getWorld().getName() );

        Location from = e.getFrom();
        Location to = e.getTo();

        ICIsland fromIsland = icWorld.getIslandAt( from.getBlockX(), from.getBlockZ() );
        ICIsland toIsland = icWorld.getIslandAt( to.getBlockX(), to.getBlockZ() );

        if( toIsland == null )
        {
            return;
        }

        if( ! toIsland.equals( fromIsland ) )
        {
            String name = db.getName( icWorld.getName(), toIsland.getCenter() );

            String title;
            title = ChatColor.YELLOW + name + " Island";
            if( icWorld.getBiomeAt( toIsland.getCenter() ).equals( ICBiome.OCEAN ) )
            {
                title = ChatColor.AQUA + name + " Sea";
            }

            String subtitle = "";
            if( name.equals( "Unnamed" ) )
            {
                subtitle = ChatColor.GREEN + "Name it with " + ChatColor.WHITE + "/found" + ChatColor.GREEN + " :D";
            }

            TitleMsg.send( p, title, subtitle);
        }
    }

    @EventHandler
    public void onTeleport( PlayerTeleportEvent e )
    {
        new PlayerMoveEvent( e.getPlayer(), e.getFrom(), e.getTo() );
    }
}
