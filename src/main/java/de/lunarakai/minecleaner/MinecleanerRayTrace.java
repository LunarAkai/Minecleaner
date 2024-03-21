package de.lunarakai.minecleaner;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.joml.Vector2d;
import org.joml.Vector2i;

public class MinecleanerRayTrace {
    MinecleanerPlugin plugin;
    
    Location arenaFieldCenterLocation;
    Location playerEyeLocation;

    // get center of arena field -> define as (0,0) for now
    // -> get clicked block
    // --> get clicked cell on that block
    // ---> get distance from clicked cell to arena center
    // ----> do math idk??

    /* Example on Board with Width Index 0 (9*9)
     *
     * 
     *       |                                                                |
     * ------|----------------------------------------------------------------|-- 
     * (8,x) | (8,0)  (8,1)  (8,2)  (8,3)  (8,4)  (8,5)  (8,6)  (8,7)  (8,8)  | 
     *       |                                                                |  
     * (7,x) | (7,0)  (7,1)  (7,2)  (7,3)  (7,4)  (7,5)  (7,6)  (7,7)  (7,8)  | 
     *       |                                                                |
     * (6,x) | (6,0)  (6,1)  (6,2)  (6,3)  (6,4)  (6,5)  (6,6)  (6,7)  (6,8)  | 
     *       |                                                                |
     * (5,x) | (5,0)  (5,1)  (5,2)  (5,3)  (5,4)  (5,5)  (5,6)  (5,7)  (5,8)  |
     *       |                                                                |
     * (4,x) | (4,0)  (4,1)  (4,2)  (4,3)  (4,4)  (4,5)  (4,6)  (4,7)  (4,8)  |
     *       |                                                                |
     * (3,x) | (3,0)  (3,1)  (3,2)  (3,3)  (3,4)  (3,5)  (3,6)  (3,7)  (3,8)  |
     *       |                                                                |
     * (2,x) | (2,0)  (2,1)  (2,2)  (2,3)  (2,4)  (2,5)  (2,6)  (2,7)  (2,8)  |
     *       |                                                                |
     * (1,x) | (1,0)  (1,1)  (1,2)  (1,3)  (1,4)  (1,5)  (1,6)  (1,7)  (1,8)  |
     *       |                                                                |
     * (0,x) | (0,0)  (0,1)  (0,2)  (0,3)  (0,4)  (0,5)  (0,6)  (0,7)  (0,8)  |
     * ------|----------------------------------------------------------------|--
     *       | (y,0)  (y,1)  (y,2)  (y,3)  (y,4)  (y,5)  (y,6)  (y,7)  (y,8)  |
     */

    public MinecleanerRayTrace(MinecleanerPlugin plugin, MinecleanerArena arena, Player player) {
        this.plugin = plugin;
        this.playerEyeLocation = player.getEyeLocation();

        arenaFieldCenterLocation = arena.getLocation().add(0.5, 0.5, 0.5);
    
    }

    public Vector2i getClickedCellPosition(Location rayTracedBlock) {
        Vector2d fieldCenter = new Vector2d(4.0,4.0);
        Vector2d blockVector = new Vector2d(rayTracedBlock.toVector().getX(), rayTracedBlock.toVector().getZ());

        //double lengthblockVector = blockVector.length();
        double distanceBlockFromFieldCenter = fieldCenter.sub(blockVector).length();

        



        double distanceFieldCenterPlayerEyeLocation = returnDistanceBetweenTwoLocations(playerEyeLocation, arenaFieldCenterLocation);

        return null;
    }


    private double returnDistanceBetweenTwoLocations(Location loc1, Location loc2) {
        double distance = loc1.distance(loc2);
        return distance;
    }
    
}
