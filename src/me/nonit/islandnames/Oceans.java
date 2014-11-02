package me.nonit.islandnames;

import com.github.hoqhuuep.islandcraft.api.ICLocation;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class Oceans
{
    private List<Ocean> oceans;

    public Oceans()
    {
        oceans = new ArrayList<Ocean>();
        oceans.add( new Ocean( "Tana", "Spore", -48, -32, -112, -96 ) );
        oceans.add( new Ocean( "Garlen", "Spore", 16, -32, -48, -96 ) );
        oceans.add( new Ocean( "Borea", "Spore", 80, -32, 16, -96 ) );
        oceans.add( new Ocean( "Lorian", "Spore", -48, 32, -112, -32 ) );
        oceans.add( new Ocean( "Wytte", "Spore", 16, 32, -48, -32 ) );
        oceans.add( new Ocean( "Nevba", "Spore", 80, 32, 16, -32 ) );
        oceans.add( new Ocean( "Demeter", "Spore", -48, 96, -112, 32 ) );
        oceans.add( new Ocean( "Etiros", "Spore", 16, 96, -48, 32 ) );
        oceans.add( new Ocean( "Prima", "Spore", 80, 96, 16, 32 ) );
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

        public Ocean( String name, String world, int xMax, int zMax, int xMin, int zMin )
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

        public boolean contains( ICLocation icL )
        {
            double x = icL.getX();
            double z = icL.getZ();

            World w = Bukkit.getServer().getWorld( world );
            Location l = new Location( w, x, 60, z );
            Chunk c = w.getChunkAt( l );

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
