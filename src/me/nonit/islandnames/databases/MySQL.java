package me.nonit.islandnames.databases;

import me.nonit.islandnames.IslandNames;
import org.bukkit.configuration.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL extends SQL
{
    public MySQL( IslandNames plugin )
    {
        super(plugin);
    }

    protected Connection getNewConnection()
    {
        Configuration config = plugin.getConfig();

        try
        {
            Class.forName("com.mysql.jdbc.Driver");

            String url = "jdbc:mysql://" + config.getString("host") + ":" + config.getString("port") + "/" + config.getString("database");

            return DriverManager.getConnection( url, config.getString( "user" ), config.getString( "password" ) );
        }
        catch (Exception e)
        {
            return null;
        }
    }

    public String getName()
    {
        return "MySQL";
    }
}