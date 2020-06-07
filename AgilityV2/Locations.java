package net.runelite.client.plugins.AgilityV2;

import com.google.common.collect.ImmutableSet;
import net.runelite.api.ObjectID;

import java.util.Set;

public class Locations {
    private final int objectID;
    private final int x1;
    private final int x2;
    private final int y1;
    private final int y2;
    private final int z;

    //private final List<Integer> coordinates;

    private static final Set<Locations> AgilityLocation = ImmutableSet.of(
            new Locations(14413, 3214,3410,3219,3419,3)
    );
    //ROUGH_WALL_14412, CLOTHES_LINE, GAP_14414, WALL_14832, GAP_14833, GAP_14834, GAP_14835, LEDGE_14836, EDGE,
    public Locations(int objectID,int x1,int y1, int x2, int y2, int z) {
        this.objectID = objectID;
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
        this.z = z;
    }

    public static Set<Locations> getAgilityLocation(){return AgilityLocation;}
    public int getObjectID(){return objectID;}
    public static Locations forPlayerLocation(int x, int y, int z){
        for (Locations locations : AgilityLocation){
            if(x >= locations.x1 && x <= locations.x2 && y >= locations.y1 && y <= locations.y2 && z == locations.z){
                return locations;
            }
        }
        return null;
    }
}
