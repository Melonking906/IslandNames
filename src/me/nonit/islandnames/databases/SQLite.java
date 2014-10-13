package me.nonit.islandnames.databases;

import me.nonit.islandnames.IslandNames;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite extends SQL
{
    private final IslandNames plugin;

    public SQLite( IslandNames plugin )
    {
        super(plugin);

        this.plugin = plugin;
    }

    protected Connection getNewConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");

            return DriverManager.getConnection( "jdbc:sqlite:" + new File( plugin.getDataFolder(), "names.db" ).getAbsolutePath() );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "SQLite";
    }
}
