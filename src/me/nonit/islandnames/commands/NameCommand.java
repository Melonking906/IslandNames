package me.nonit.islandnames.commands;

import com.github.hoqhuuep.islandcraft.api.ICBiome;
import com.github.hoqhuuep.islandcraft.api.ICIsland;
import com.github.hoqhuuep.islandcraft.api.ICWorld;
import com.github.hoqhuuep.islandcraft.api.IslandCraft;
import me.nonit.islandnames.IslandNames;
import me.nonit.islandnames.TitleMsg;
import me.nonit.islandnames.databases.SQL;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NameCommand implements CommandExecutor
{
    private static final SQL db = IslandNames.getDb();
    private static final Economy e = IslandNames.getEconomy();
    private static final String PREFIX = ChatColor.YELLOW + "[Loy]" + ChatColor.GREEN + " ";
    private static final Double COST = 500.0;

    private IslandCraft ic;

    public NameCommand( IslandNames p )
    {
        ic = p.getIc();
    }

    @Override
    public boolean onCommand( CommandSender sender, Command command, String s, String[] args )
    {
        if( ! (sender instanceof Player) )
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

        if( icIsland == null )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "You're in open waters, no islands here!" );
            return true;
        }

        String type = "island";
        if( icWorld.getBiomeAt( icIsland.getCenter() ).equals( ICBiome.OCEAN ) )
        {
            type = "sea";
        }
        else if( icWorld.getBiomeAt( icIsland.getCenter() ).equals( ICBiome.SWAMPLAND ) )
        {
            type = "swamp";
        }

        String islandName = db.getName( icWorld.getName(), icIsland.getCenter() );

        if( args.length < 1 )
        {
            p.sendMessage( PREFIX + "Use /name to name this area!" );
            return true;
        }

        if( ! p.hasPermission( "islandnames.name" ) )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry, but you cant name areas :(" );
            return true;
        }

        if( ! islandName.equals( "Unnamed" ) && ! p.hasPermission( "islandnames.refound" ) )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "This island is already called " + islandName + "!" );
            return true;
        }

        if( e.getBalance( p ) < COST )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "It costs " + ChatColor.WHITE + e.format( COST ) + ChatColor.RED +
                                                    " to name an island, you have " + ChatColor.WHITE + e.format( e.getBalance( p ) ) + ChatColor.RED + "!" );
            return true;
        }

        String name = args[0];
        name = ChatColor.translateAlternateColorCodes( '&', name );
        name = ChatColor.stripColor( name );
        name = WordUtils.capitalizeFully( name );

        if( name.length() > 10 )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry but names can't be longer than 10 letters!" );
            return true;
        }

        if( ! name.equals( "Unnamed" ) && db.isNameUsed( name ) )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry but there is already an area called " + ChatColor.WHITE + name + ChatColor.RED + " :(" );
            return true;
        }

        db.setName( name, icWorld.getName(), icIsland.getCenter() );

        p.sendMessage( PREFIX + "Success, this " + type + " is now officially called " + ChatColor.YELLOW + name + ChatColor.GREEN + "!" );

        if( ! name.equals( "Unnamed" ) )
        {
            p.sendMessage( PREFIX + "You payed " + ChatColor.YELLOW + e.format( COST ) + ChatColor.GREEN + " to found this " + type + "!" );

            e.withdrawPlayer( p, COST );
            e.depositPlayer( "IoCo", COST );

            String msg = ChatColor.YELLOW + name + ChatColor.GREEN + " " + type + " has been founded by " + ChatColor.YELLOW + p.getDisplayName();
            for( Player player : Bukkit.getOnlinePlayers() )
            {
                TitleMsg.send( player, "", msg );
            }

            //Give the player a cert.
            PlayerInventory inventory = p.getInventory();

            String displayName = ChatColor.GOLD + "Certificate of Founding";
            List<String> lore = new ArrayList<String>();
            lore.add( ChatColor.YELLOW + name + " " + WordUtils.capitalize( type ) );
            lore.add( ChatColor.GREEN + "Founded by " + p.getDisplayName() );
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            Date d = new Date();
            lore.add( ChatColor.YELLOW + "" + dateFormat.format( d ) );

            ItemStack myItem = new ItemStack( Material.PAPER, 1 );
            ItemMeta im = myItem.getItemMeta();
            im.setDisplayName( displayName );
            im.setLore( lore );
            im.addEnchant( Enchantment.LUCK, 1, true );
            myItem.setItemMeta( im );

            inventory.addItem( myItem );
        }

        return true;
    }
}
