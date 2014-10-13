package me.nonit.islandnames;

import me.nonit.islandnames.commands.FoundCommand;
import me.nonit.islandnames.databases.MySQL;
import me.nonit.islandnames.databases.SQL;
import me.nonit.islandnames.databases.SQLite;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.hoqhuuep.islandcraft.api.IslandCraft;
import com.github.hoqhuuep.islandcraft.bukkit.IslandCraftPlugin;

import java.util.HashSet;
import java.util.Set;

public class IslandNames extends JavaPlugin
{
    private Set<SQL> databases;
    private static SQL db;

    private IslandCraft islandCraft;

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
            islandCraft = islandCraftPlugin.getIslandCraft();
        }
        catch( final Exception e )
        {
            getLogger().severe( "Could not find IslandCraft, please make sure plugin is installed correctly." );
            setEnabled( false );
            return;
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
    }

    @Override
    public void onDisable()
    {
        islandCraft = null;
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

    public void log( String message )
    {
        getLogger().info( message );
    }

    public IslandCraft getIslandCraft()
    {
        return islandCraft;
    }

    public static SQL getDb()
    {
        return db;
    }
}