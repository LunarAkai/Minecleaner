package de.lunarakai.minecleaner;

import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import com.google.common.base.Preconditions;
import de.lunarakai.minecleaner.game.Game;

public class MinecleanerArena {
    private final MinecleanerPlugin plugin;
    private final String name;
    private final Location location;
    private final Location centerLocation;
    private final BlockFace orientation;
    private ArenaStatus arenaStatus = ArenaStatus.INACTIVE;
    private UUID[] displayEntities;
    
    private Player currentPlayer;
    private long currentGameStartTime;
    private Game currentMinecleanerGame;

    public MinecleanerArena(MinecleanerPlugin plugin, ConfigurationSection arenaSection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(arenaSection.getString("name"));
        this.location = Preconditions.checkNotNull(arenaSection.getLocation("location"));
        BlockFace orientation = BlockFace.NORTH;
        try {
            orientation = BlockFace.valueOf(arenaSection.getString("orientation"));
        } catch(IllegalArgumentException ignored) {

        }
        this.orientation = orientation;

        this.centerLocation = location.clone().add(0.5, 0, 0.5);
        displayEntities = new UUID[Game.width * Game.height];
    } 

    public MinecleanerArena(MinecleanerPlugin plugin, String name, Location location, BlockFace orientation) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(name, "name");
        this.location = Preconditions.checkNotNull(location, "location");
        Preconditions.checkArgument(Math.abs(orientation.getModX()) + Math.abs(orientation.getModZ()) == 1, "no cardinal direction");
        this.orientation = orientation;
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;
        this.centerLocation = location.clone().add(0.5, 0, 0.5);
    }

    public void save(ConfigurationSection arenaSection) {
        arenaSection.set("name", this.name);
        arenaSection.set("location", this.location);
        arenaSection.set("orientation", this.orientation.name());
    }

    public void startNewGame() {
        currentMinecleanerGame.start();
        arenaStatus = ArenaStatus.PLAYING;
    }

    public void removePlayer() {
        this.arenaStatus = ArenaStatus.INACTIVE;
        this.currentPlayer = null;
    }

    public String getName() {
        return name;
    }

    public boolean hasPlayer() {
        return currentPlayer != null;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Location getLocation() {
        return location;
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
    }

    public void flagCell(int x, int y) {
        if(currentMinecleanerGame != null) {
            int id = x + y * 8;
            boolean unflaggedCell = currentMinecleanerGame.flag(x, y);
            if(!unflaggedCell) {
                // todo set flag head on block display
            } else {
                // todo set normal head on block display
            }
        }
    }

    public void revealCell(int x, int y) {
        if(currentMinecleanerGame != null) {
            int id = x + y * 8;
            // todo check if cell is flagged already
            currentMinecleanerGame.reveal(x, y);
            // todo update block of blockdisplay
        }
    }
    
}
