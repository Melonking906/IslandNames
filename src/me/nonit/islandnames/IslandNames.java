package me.nonit.islandnames;

import me.nonit.islandnames.commands.FoundCommand;
import me.nonit.islandnames.databases.MySQL;
import me.nonit.islandnames.databases.SQL;
import me.nonit.islandnames.databases.SQLite;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.hoqhuuep.islandcraft.api.IslandCraft;
import com.github.hoqhuuep.islandcraft.bukkit.IslandCraftPlugin;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class IslandNames extends JavaPlugin
{
    private static SQL db;
    private static List<String> worlds;
    private static Economy economy = null;

    private Set<SQL> databases;
    private IslandCraft ic;

    public IslandNames()
    {
        databases = new HashSet<SQL>();
    }

    @Override
    public void onEnable()
    {
        try
        {
            final IslandCraftPlugin islandCraftPlugin = getPlugin( IslandCraftPlugin.class );
            ic = islandCraftPlugin.getIslandCraft();
        }
        catch( final Exception e )
        {
            getLogger().severe( "Could not find IslandCraft, please make sure plugin is installed correctly." );
            setEnabled( false );
            return;
        }

        if( ! setupEconomy() )
        {
            getLogger().info( "There is no econ plugin installed..." );
            setEnabled( false );
        }

        databases.add( new MySQL( this ) );
        databases.add( new SQLite( this ) );

        saveDefaultConfig();
        setupDatabase();

        PluginManager pm = getServer().getPluginManager();

        if( ! db.checkConnection() )
        {
            log( "Error with DATABASE" );
            pm.disablePlugin( this );
        }

        getCommand("found").setExecutor( new FoundCommand(this) );

        pm.registerEvents( new PlayerListener( this ), this );

        worlds = getConfig().getStringList( "worlds" );
    }

    @Override
    public void onDisable()
    {
        ic = null;
        db.disconnect();
    }

    private boolean setupDatabase()
    {
        String type = getConfig().getString("type");

        db = null;

        for ( SQL database : databases )
        {
            if ( type.equalsIgnoreCase( database.getConfigName() ) )
            {
                db = database;

                log( "Database set to " + database.getConfigName() + "." );

                break;
            }
        }

        if ( db == null)
        {
            log( "Database type does not exist!" );

            return false;
        }

        return true;
    }

    private boolean setupEconomy()
    {
        RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
        if (economyProvider != null)
        {
            economy = economyProvider.getProvider();
        }

        return (economy != null);
    }

    public void log( String message )
    {
        getLogger().info( message );
    }

    public static Economy getEconomy()
    {
        return economy;
    }

    public IslandCraft getIc()
    {
        return ic;
    }

    public static SQL getDb()
    {
        return db;
    }

    public static boolean isIslandWorld( String world )
    {
        return worlds.contains( world );
    }
}