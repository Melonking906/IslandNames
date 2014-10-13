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

public class FoundCommand implements CommandExecutor
{
    private static final SQL db = IslandNames.getDb();
    private static final Economy e = IslandNames.getEconomy();
    private static final String PREFIX = ChatColor.YELLOW + "[Loy]" + ChatColor.GREEN + " ";
    private static final Double COST = 500.0;

    private IslandCraft ic;

    public FoundCommand( IslandNames p )
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

        boolean isSea = false;
        if( icWorld.getBiomeAt( icIsland.getCenter() ).equals( ICBiome.OCEAN ) )
        {
            isSea = true;
        }

        String islandName = db.getName( icWorld.getName(), icIsland.getCenter() );

        if( args.length < 1 )
        {
            if( isSea )
            {
                p.sendMessage( PREFIX + "You are in the " + ChatColor.YELLOW + islandName + ChatColor.GREEN + " sea!" );
            }
            else
            {
                p.sendMessage( PREFIX + "You are on " + ChatColor.YELLOW + islandName + ChatColor.GREEN + " island!" );
            }
            return true;
        }

        if( ! p.hasPermission( "islandnames.found" ) )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry, but you cant name islands :(" );
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
                                                    " to found and island, you have " + ChatColor.WHITE + e.format( e.getBalance( p ) ) + ChatColor.RED + "!" );
            return true;
        }

        String name = args[0];
        name = ChatColor.translateAlternateColorCodes( '&', name );
        name = ChatColor.stripColor( name );
        name = WordUtils.capitalizeFully( name );

        if( name.length() > 8 )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry but names cant be longer than 8 letters!" );
            return true;
        }

        if( ! name.equals( "Unnamed" ) && db.isNameUsed( name ) )
        {
            p.sendMessage( PREFIX + ChatColor.RED + "Sorry but there is already an area called " + ChatColor.WHITE + name + ChatColor.RED + " :(" );
            return true;
        }

        db.setName( name, icWorld.getName(), icIsland.getCenter() );

        String type = "island";
        if( isSea )
        {
            type = "sea";
        }

        p.sendMessage( PREFIX + "Success, this " + type + " is now officially called " + ChatColor.YELLOW + name + ChatColor.GREEN + "!" );

        e.withdrawPlayer( p, COST );
        e.depositPlayer( "IoCo", COST );

        if( ! isSea && ! name.equals( "Unnamed" ) )
        {
            for( Player player : Bukkit.getOnlinePlayers() )
            {
                String msg = ChatColor.YELLOW + name + ChatColor.GREEN + " island has been founded by " + ChatColor.YELLOW + p.getDisplayName();
                TitleMsg.send( player, "", msg );
            }

            //Give the player a cert.
            PlayerInventory inventory = p.getInventory();

            String displayName = ChatColor.GOLD + "Certificate of Founding";
            List<String> lore = new ArrayList<String>();
            lore.add( ChatColor.YELLOW + name + " Island" );
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
