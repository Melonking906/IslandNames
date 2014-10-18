package me.nonit.islandnames;

import com.github.hoqhuuep.islandcraft.api.ICLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Oceans
{
    private List<Ocean> oceans;

    public Oceans()
    {
        oceans = new ArrayList<Ocean>();
        oceans.add( new Ocean( "Wet", "Spore", 16, 32, -48, -32 ) );
    }

    public String getOceanName( ICLocation l )
    {
        for( Ocean ocean : oceans )
        {
            if( ocean.contains( l ) )
            {
                return ocean.getName();
            }
        }

        return null;
    }

    private class Ocean
    {
        private String name;
        private String world;
        private int xMax;
        private int zMax;
        private int xMin;
        private int zMin;

        public Ocean( String name, String world ,int xMax, int zMax, int xMin, int zMin )
        {
            this.name = name;
            this.world = world;
            this.xMax = xMax;
            this.zMax = zMax;
            this.xMin = xMin;
            this.zMin = zMin;
        }

        public String getName()
        {
            return name;
        }

        public boolean contains( ICLocation l )
        {
            int x = l.getX();
            int z = l.getZ();

            World w = Bukkit.getServer().getWorld( world );
            Chunk c = w.getChunkAt( x, z );

            if( c.getX() <= xMax && c.getX() >= xMin )
            {
                if( c.getZ() <= zMax && c.getZ() >= zMin )
                {
                    return true;
                }
            }

            return false;
        }
    }
}
