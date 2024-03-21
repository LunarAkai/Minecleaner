package de.lunarakai.minecleaner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.BlockDisplay;
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector3f;
import com.google.common.base.Preconditions;
import de.lunarakai.minecleaner.game.BoardSize;
import de.lunarakai.minecleaner.game.Game;

public class MinecleanerArena {
    private final MinecleanerPlugin plugin;
    private final String name;
    private final Location location;
    private final Location centerLocation;
    private int widthIndex = 0;
    private final BlockFace orientation;
    private ArenaStatus arenaStatus = ArenaStatus.INACTIVE;
    private UUID[] blockDisplays = new UUID[81]; // todo needs to be of size boardSizes[widthIndex]
    
    private Player currentPlayer;
    private long currentGameStartTime;
    private Game currentMinecleanerGame;

    private final Location tempLoc = new Location(null, 0, 0, 0);

    public MinecleanerArena(MinecleanerPlugin plugin, ConfigurationSection arenaSection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(arenaSection.getString("name"));
        this.location = Preconditions.checkNotNull(arenaSection.getLocation("location"));
        this.widthIndex = Preconditions.checkNotNull(arenaSection.getInt("fieldwidth"));

        BlockFace orientation = BlockFace.NORTH;
        try {
            orientation = BlockFace.valueOf(arenaSection.getString("orientation"));
        } catch(IllegalArgumentException ignored) {

        }
        this.orientation = orientation;

        this.centerLocation = location.clone().add(0.5, 0, 0.5);

        List<String> list = arenaSection.getStringList("blockdisplays");
        for(int i = 0; i < list.size(); i++) {
            String blockDisplay = list.get(i);
            if(blockDisplay != null) {
                blockDisplays[i] = UUID.fromString(blockDisplay);
            }
        }
    } 

    public MinecleanerArena(MinecleanerPlugin plugin, String name, Location location, int widthIndex, BlockFace orientation) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(name, "name");
        this.location = Preconditions.checkNotNull(location, "location");
        this.widthIndex = Preconditions.checkNotNull(widthIndex, ("fieldwidth"));

        Preconditions.checkArgument(Math.abs(orientation.getModX()) + Math.abs(orientation.getModZ()) == 1, "no cardinal direction");
        this.orientation = orientation;
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;
        this.centerLocation = location.clone().add(0.5, 0, 0.5);
    }

    public void generateBackgroundBlocks() {
        World world = location.getWorld();
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;
        Location loc = location.clone();

        BlockData block0 = Material.NETHER_BRICKS.createBlockData();
        BlockData block1 = Material.BRICKS.createBlockData();
        for (int fx = -1; fx < 2; fx++) {
            for (int fy = -1; fy < 2; fy++) {
                loc.set(location.getX() + d1x * fx, location.getY() + fy, location.getZ() + d1z * fx);
                boolean f = (fx + fy) % 2 == 0;
                world.setBlockData(loc, f ? block0 : block1);
            }
        }
    }

    public void generateBlockDisplays() {
        World world = location.getWorld();
        for(UUID id : blockDisplays) {
            if(id != null) {
                Entity blockdisplay = world.getEntity(id);
                if(blockdisplay instanceof Display) {
                    blockdisplay.remove();
                }
            }
        }
        Arrays.fill(blockDisplays, null);

        float rotation0 = 0;
        if(orientation == BlockFace.EAST) {
            rotation0 = 90;
        } else if(orientation == BlockFace.SOUTH) {
            rotation0 = 180;
        } else if(orientation == BlockFace.WEST) {
            rotation0 = 270;
        }

        float rotation = rotation0;
        
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;

        // QWing sudoku = plugin.getGeneratorThread().getSudoku(Difficulty.EASY, true);
        // int[] puzzle = sudoku == null ? new int[81] : sudoku.getPuzzle();

        Location loc = location.clone();
        for(int fx = 0; fx < 9; fx++) {
            final int fxf = fx;
            for(int fz = 0; fz < 9; fz++) {
                final int fzf = fz;
                // Todo not correctly alligned at different orientations (other than NORTH)

                loc.set(location.getX() + 0.11 - (d1x * fz) / 3.0 + d0x * 0.501 + d1x * 1.847, location.getY() - 0.9725 + fxf / 3.0, location.getZ() + 0.45 - (d1z * fz) / 3.0 + d0z * 0.501 + d1z * 1.847);

                Display blockDisplay = world.spawn(loc, BlockDisplay.class, blockdisplay -> {
                    Transformation transformation = blockdisplay.getTransformation();
                    Transformation newTransform;
                    Vector3f newTranslationScale = new Vector3f(0.30f, 0.25f, 0.25f);
                    newTransform = new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation(), 
                        newTranslationScale,
                        transformation.getRightRotation());

                    blockdisplay.setTransformation(newTransform);
                    blockdisplay.setRotation(rotation + 90, 0);
                    blockdisplay.setBlock(Material.BEDROCK.createBlockData());
                });
                if(blockDisplay != null) {
                    blockDisplays[fxf + fzf * 9] = blockDisplay.getUniqueId();
                }
            }
        }
        
        // show Displays
    }

    public void save(ConfigurationSection arenaSection) {
        arenaSection.set("name", this.name);
        arenaSection.set("location", this.location);
        arenaSection.set("fieldwidth", this.widthIndex);
        arenaSection.set("orientation", this.orientation.name());
        List<String> blockDisplays = new ArrayList<>();
        for(UUID uuid : this.blockDisplays) {
            blockDisplays.add(uuid == null ? null : uuid.toString());
        }
        arenaSection.set("blockdisplays", blockDisplays);
    }

    public void startNewGame() {
        currentMinecleanerGame = new Game();
        currentMinecleanerGame.start();
        arenaStatus = ArenaStatus.PLAYING;
    }

    public void addJoiningPlayer(Player player) {
        Preconditions.checkNotNull(player);
        Preconditions.checkState(arenaStatus == ArenaStatus.INACTIVE);
        this.arenaStatus = ArenaStatus.CONFIRM_PLAYING;
        this.currentPlayer = player;
    } 

    public void removePlayer() {
        this.arenaStatus = ArenaStatus.INACTIVE;
        this.currentPlayer = null;
    }

    // block displays dont get removed
    public void removeBlockDisplays() {
        World world = location.getWorld();
        for(int fx = 0; fx < 9; fx++) {
            for(int fy = 0; fy < 9; fy++) {
                UUID blockDisplayUuid = blockDisplays[fx + fy * 9];
                Entity blockDisplayEntity = blockDisplayUuid != null ? world.getEntity(blockDisplayUuid) : null;
                //if(blockDisplayEntity instanceof BlockDisplay blockDisplay) {
                    blockDisplayEntity.remove(); // Null Pointer after restart
                //}
            }
        }
    }

    public void flagCell(int x, int y) {
        if(currentMinecleanerGame != null) {
            int id = x + y * 9;
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
            int id = x + y * 9;
            // todo check if cell is flagged already
            currentMinecleanerGame.reveal(x, y);
            // todo update block of blockdisplay
        }
    }

    private int matchWidthIndexToActualWidth(int widthIndex) {
        switch (widthIndex) {
            case 0:
                return BoardSize.boardSizes[0];
            case 1:
                return BoardSize.boardSizes[1];
            case 2:
                return BoardSize.boardSizes[2];
            default:
                return BoardSize.boardSizes[0];
        }
    }

    public List<Location> getBlocks() {
        ArrayList<Location> blocks = new ArrayList<>();
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;

        Location loc = location.clone();
        for(int fx = -2; fx < 1; fx++) {
            for(int fy = -1; fy < 2; fy++) {
                loc.set(location.getX() + d1x + fx, location.getY() + fy, location.getZ() + d1z * fx);
                blocks.add(loc.clone());
            }
        }

        return blocks;
    }

    public boolean isTooFarAway(Player player) {
        if(player.getWorld() != location.getWorld()) {
            return true;
        }
        player.getLocation(tempLoc);
        double dist = tempLoc.distanceSquared(centerLocation);
        return dist > 64.0;
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

    public BlockFace getOrientation() {
        return orientation;
    }

    public ArenaStatus getArenaStatus() {
        return arenaStatus;
    }

    public UUID[] getBlockDisplays() {
        return blockDisplays;
    }
    
}
