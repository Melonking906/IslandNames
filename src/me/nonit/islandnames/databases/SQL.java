package me.nonit.islandnames.databases;

import com.github.hoqhuuep.islandcraft.api.ICLocation;
import me.nonit.islandnames.IslandNames;

import java.sql.*;
import java.util.*;

public abstract class SQL
{
    private Connection connection;
    private HashMap<ICLocation, String> cache = new HashMap<ICLocation, String>();

    protected IslandNames plugin;

    public SQL( IslandNames plugin )
    {
        this.plugin = plugin;

        plugin.getServer().getScheduler().runTaskTimerAsynchronously( plugin, new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    if( connection != null && ! connection.isClosed() )
                    {
                        connection.createStatement().execute( "/* ping */ SELECT 1" );
                        updateTables();
                    }
                }
                catch( SQLException e )
                {
                    connection = getNewConnection();
                }
            }
        }, 60 * 20, 60 * 20 );
    }

    protected abstract Connection getNewConnection();

    protected abstract String getName();

    public String getConfigName()
    {
        return getName().toLowerCase().replace(" ", "");
    }

    private ArrayList<HashMap<String,String>> query( String sql, boolean hasReturn )
    {
        if( ! checkConnection() )
        {
            plugin.getLogger().info( "Error with database" );
            return null;
        }

        PreparedStatement statement;
        try
        {
            statement = connection.prepareStatement( sql );

            if( ! hasReturn )
            {
                statement.execute();
                return null;
            }

            ResultSet set = statement.executeQuery();

            ResultSetMetaData md = set.getMetaData();
            int columns = md.getColumnCount();

            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>( 50 );

            while( set.next() )
            {
                HashMap<String,String> row = new HashMap<String,String>( columns );
                for( int i = 1; i <= columns; ++i )
                {
                    row.put( md.getColumnName( i ), set.getObject( i ).toString() );
                }
                list.add( row );
            }

            if( list.isEmpty() )
            {
                return null;
            }

            return list;
        }
        catch( SQLException e )
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkConnection()
    {
        try
        {
            if( connection == null || connection.isClosed() )
            {
                connection = getNewConnection();

                if( connection == null || connection.isClosed() )
                {
                    return false;
                }
            }
        }
        catch( SQLException e )
        {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    private void updateTables()
    {
        query( "CREATE TABLE IF NOT EXISTS islandnames_names (" +
                "world_name varchar(255) NOT NULL," +
                "center_x int(11) NOT NULL," +
                "center_z int(11) NOT NULL," +
                "name varchar(64) NOT NULL," +
                "PRIMARY KEY (world_name,center_x,center_z)" +
                ");", false );
    }

    public void disconnect()
    {
        cache.clear();

        try
        {
            if (connection != null)
            {
                connection.close();
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public String getName( String world, ICLocation center )
    {
        String name;

        if( cache.containsKey( center ) )
        {
            return cache.get( center );
        }

        ArrayList<HashMap<String,String>> data = query( "SELECT name FROM islandnames_names WHERE world_name = '" +
                                                        world + "' AND center_x = '" + center.getX() +
                                                        "' AND center_z = '" + center.getZ() + "';", true );
        if( data == null )
        {
            name = "Unnamed";
            setName( name, world, center );
        }
        else
        {
            name = data.get( 0 ).get( "name" );
        }

        cache.put( center, name );

        return name;
    }

    public void unsetName( String world, ICLocation center )
    {
        query( "DELETE FROM islandnames_names WHERE world_name = '" +
                world + "' AND center_x = '" + center.getX() +
                "' AND center_z = '" + center.getZ() + "';", false );
        
        cache.remove( center );
    }

    public void setName( String name, String world, ICLocation center )
    {
        unsetName( world, center );

        name = name.replace( "'", "''" );

        query( "INSERT INTO islandnames_names (name, world_name, center_x, center_z) " +
                "VALUES ('" + name + "','" + world + "'," + center.getX() + "," + center.getZ() + ");", false );

        cache.put( center, name );
    }

    public boolean isNameUsed( String name )
    {
        if( cache.containsValue( name ) )
        {
            return true;
        }

        name = name.replace( "'", "''" );

        ArrayList<HashMap<String,String>> data = query( "SELECT name FROM islandnames_names WHERE name = '" + name + "';", true );

        return data != null;
    }
}
