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
import org.bukkit.entity.Display;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.util.Transformation;
import org.joml.Vector2i;
import org.joml.Vector3f;
import com.google.common.base.Preconditions;
import de.lunarakai.minecleaner.game.BoardSize;
import de.lunarakai.minecleaner.game.Cell;
import de.lunarakai.minecleaner.game.Game;
import de.lunarakai.minecleaner.utils.MinecleanerHeads;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class MinecleanerArena {
    private final MinecleanerPlugin plugin;
    private final String name;
    private final Location location;
    private final Location centerLocation;
    private int widthIndex = 0;
    private final BlockFace orientation;
    private ArenaStatus arenaStatus = ArenaStatus.INACTIVE;
    private UUID[] blockDisplays;
    // private UUID[] textDisplays;
    
    private int flagsPlaced = 0;

    private Player currentPlayer;
    private long currentGameStartTime;
    private Game currentMinecleanerGame;

    private final Location tempLoc = new Location(null, 0, 0, 0);

    public static final MinecleanerHeads[] MINECLEANER_HEADS = {
        MinecleanerHeads.MINESWEEPER_TILE_0,
        MinecleanerHeads.MINESWEEPER_TILE_1,
        MinecleanerHeads.MINESWEEPER_TILE_2,
        MinecleanerHeads.MINESWEEPER_TILE_3,
        MinecleanerHeads.MINESWEEPER_TILE_4,
        MinecleanerHeads.MINESWEEPER_TILE_5,
        MinecleanerHeads.MINESWEEPER_TILE_6,
        MinecleanerHeads.MINESWEEPER_TILE_7,
        MinecleanerHeads.MINESWEEPER_TILE_8,
        MinecleanerHeads.MINESWEEPER_TILE_FLAG,
        MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN,
    };

    public MinecleanerArena(MinecleanerPlugin plugin, ConfigurationSection arenaSection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(arenaSection.getString("name"));
        this.location = Preconditions.checkNotNull(arenaSection.getLocation("location"));
        this.widthIndex = Preconditions.checkNotNull(arenaSection.getInt("fieldwidth"));
        this.blockDisplays = new UUID[BoardSize.boardSizes[widthIndex] * BoardSize.boardSizes[widthIndex]];
        // this.textDisplays = new UUID[1];

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

        // List<String> textList = arenaSection.getStringList("textdisplay");
        // for(int i = 0; i < textList.size(); i++) {
        //     String textString = list.get(i);
        //     if(textString != null) {
        //         textDisplays[0] = UUID.fromString(textString);
        //     }
        // } 
    } 

    public MinecleanerArena(MinecleanerPlugin plugin, String name, Location location, int widthIndex, BlockFace orientation) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(name, "name");
        this.location = Preconditions.checkNotNull(location, "location");
        this.widthIndex = Preconditions.checkNotNull(widthIndex, ("fieldwidth"));
        this.blockDisplays = new UUID[BoardSize.boardSizes[widthIndex] * BoardSize.boardSizes[widthIndex]];
        //this.textDisplays = new UUID[1];

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

        for (int fx = -1 - widthIndex; fx < 2 ; fx++) {
            for (int fy = -1; fy < 2 + widthIndex; fy++) {
                loc.set(location.getX() + d1x * fx, location.getY() + fy, location.getZ() + d1z * fx);
                boolean f = (fx + fy) % 2 == 0;
                world.setBlockData(loc, f ? block0 : block1);
            }
        }
    }

    /*
     *  Bei Größen WidthIndex 1 + 2 -> Mitte = ein Block nach Links unten versetzt 
     */
    public void generateBlockDisplays() {
        int size = BoardSize.boardSizes[widthIndex];

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
        double eastWestGapFixX = 0.0;
        double eastWestGapFixZ = 0.0;

        double southGapFixX = 0.0;
        double southGapFixZ = 0.0;
        if(orientation == BlockFace.EAST) {
            rotation0 = 90;
            eastWestGapFixX = 0.55;
            eastWestGapFixZ = -0.46;
        } else if(orientation == BlockFace.SOUTH) {
            rotation0 = 180;
            southGapFixX = 1.02;
            southGapFixZ = 0.1;
        } else if(orientation == BlockFace.WEST) {
            rotation0 = 270;
            eastWestGapFixX = 0.5;
            eastWestGapFixZ = 0.5725;
        }

        float rotation = rotation0;
        
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;

        Location loc = location.clone();

        // todo: felder bisschen dichter an die wand
        for(int fx = 0; fx < size; fx++) {
            final int fxf = fx;
            for(int fz = 0; fz < size; fz++) {
                final int fzf = fz;

                loc.set(location.getX() - 0.016 + eastWestGapFixX + southGapFixX - (d1x * fz) / 3.0 + d0x * 0.55 + d1x * 1.847, 
                    location.getY() - 0.8225 + fxf / 3.0, 
                    location.getZ() + 0.45 + eastWestGapFixZ  + southGapFixZ - (d1z * fz) / 3.0 + d0z * 0.55 + d1z * 1.847);

                Display blockDisplay = world.spawn(loc, ItemDisplay.class, blockdisplay -> {
                    Transformation transformation = blockdisplay.getTransformation();
                    Transformation newTransform;
                    Vector3f newTranslationScale = new Vector3f(0.60f, 0.60f, 0.60f);
                    newTransform = new Transformation(
                        transformation.getTranslation(),
                        transformation.getLeftRotation(), 
                        newTranslationScale,
                        transformation.getRightRotation());

                    blockdisplay.setTransformation(newTransform);
                    blockdisplay.setRotation(rotation, -90);
                    
                    blockdisplay.setItemStack(MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN.getHead());
                });


                if(blockDisplay != null) {
                    blockDisplays[fxf + fzf * size] = blockDisplay.getUniqueId();
                }
            }
        }

        // Location textLocation  = location.clone();
        // TextDisplay textDisplay = world.spawn(textLocation.add(-1, 2 + widthIndex, -0.25), TextDisplay.class, textdisplay -> {
        //     Transformation transformation = textdisplay.getTransformation();
        //     Transformation newTransformation;
        //     newTransformation = new Transformation(
        //         transformation.getTranslation(), 
        //         transformation.getLeftRotation(), 
        //         transformation.getTranslation(), 
        //         transformation.getRightRotation());
            
        //     textdisplay.setTransformation(newTransformation);
        //     textdisplay.setRotation(rotation, 0);

        //      billboardmode = fixed
        //     textdisplay.setVisibleByDefault(true);
        //     textdisplay.setDisplayHeight(3);
        //     textdisplay.setDisplayWidth(9);
        //     textdisplay.setText("Minecleaner");
        // });

        // if(textDisplay != null) {
        //     textDisplays[0] = textDisplay.getUniqueId();
        // }
        
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
        // List<String> textd = new ArrayList<>();
        // for(UUID uuid : this.textDisplays) {
        //     textd.add(uuid == null ? null : uuid.toString());
        // }
        // arenaSection.set("textdisplay", textd);
    }

    private void setDiplayBlock(int x, int y, MinecleanerHeads head) {
        int size = BoardSize.boardSizes[widthIndex];

        UUID blockDisplayId = blockDisplays[x + y * size];
        Entity blockDisplay = blockDisplayId != null ? location.getWorld().getEntity(blockDisplayId) : null;
        if(blockDisplay instanceof ItemDisplay) {
            ItemDisplay display = (ItemDisplay) blockDisplay;
            display.setItemStack(head.getHead());
        }
    }

    public void startNewGame() {
        currentMinecleanerGame = new Game(plugin, BoardSize.boardSizes[widthIndex], BoardSize.mineCounter[widthIndex]);
        currentMinecleanerGame.start();
        flagsPlaced = 0;
        arenaStatus = ArenaStatus.PLAYING;
    }

    public void addJoiningPlayer(Player player) {
        Preconditions.checkNotNull(player);
        Preconditions.checkState(arenaStatus == ArenaStatus.INACTIVE);
        this.arenaStatus = ArenaStatus.CONFIRM_PLAYING;
        this.currentPlayer = player;
    } 

    public void removePlayer() {
        int size = BoardSize.boardSizes[widthIndex];

        this.arenaStatus = ArenaStatus.INACTIVE;
        this.currentPlayer = null;
        this.currentMinecleanerGame = null;
        
        for(int x = 0; x < size; x++) {
            for(int y = 0; y < size; y++) {
                setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN);
            }
        }
    }

    public void removeBlockDisplays() {
        int size = BoardSize.boardSizes[widthIndex];

        World world = location.getWorld();
        for(int fx = 0; fx < size; fx++) {
            for(int fy = 0; fy < size; fy++) {
                UUID blockDisplayUuid = blockDisplays[fx + fy * size];
                Entity blockDisplayEntity = blockDisplayUuid != null ? world.getEntity(blockDisplayUuid) : null;
                if(blockDisplayEntity instanceof Display blockdisplay) {
                    blockDisplayEntity.remove(); 
                }
            }
        }
    }

    public void flagCell(int x, int y) {
        if(currentMinecleanerGame != null && !currentMinecleanerGame.gameover) {
            Cell cell = currentMinecleanerGame.getCell(x, y);
            if(!cell.isRevealed()) {
                Player player = this.currentPlayer;
            
                currentMinecleanerGame.flag(x, y);
                if(currentMinecleanerGame.gameover) {
                    plugin.getManager().handleGameover(player, this, true);
                }
        
                if(cell.isFlagged() == true) {
                    flagsPlaced = flagsPlaced + 1;
                    sendActionBarMessage(player);
                    setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_FLAG);
                } 

                if(cell.isFlagged() == false) {
                    flagsPlaced = flagsPlaced - 1;
                    sendActionBarMessage(player);
                    setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN);
                }
            }
            
        }
    }

    public void revealCell(int x, int y) {
        if(currentMinecleanerGame != null && !currentMinecleanerGame.gameover) {
            Cell cell = currentMinecleanerGame.getCell(x, y);
            if(!cell.isFlagged()) {           
                Player player = this.currentPlayer;
                
                currentMinecleanerGame.reveal(x, y);
                setBlockForCellType(x, y, cell);
                
                if(currentMinecleanerGame.gameover) {
                    plugin.getManager().handleGameover(player, this, !(cell.isRevealed() && cell.isExploded()));
                } else {
                    sendActionBarMessage(player);
                }
                
                ArrayList<Cell> floodedCells = currentMinecleanerGame.getfloodedCells();
                if(floodedCells != null) {                
                    for(int i = 0; i < floodedCells.size(); i++) {
                        Vector2i pos = floodedCells.get(i).position;
                        setBlockForCellType(pos.x, pos.y, floodedCells.get(i));
                    }
                }
            }
        }
    }

    private void sendActionBarMessage(Player player) {
        TextComponent textComponent = new TextComponent("Flaggen gesetzt: " + flagsPlaced + "  Minen insgesamt: " + BoardSize.mineCounter[widthIndex]);
        textComponent.setColor(ChatColor.GOLD);
        player.sendMessage(ChatMessageType.ACTION_BAR, textComponent);
    }

    public void showMines() {
        ArrayList<Cell> explodedCells = currentMinecleanerGame.getExplodedCells();
        if(explodedCells != null) {
            for(int i = 0; i < explodedCells.size(); i++) {
                Vector2i pos = explodedCells.get(i).position;
                setBlockForCellType(pos.x, pos.y, explodedCells.get(i));
            }
        }

    }

    private void setBlockForCellType(int x, int y, Cell cell) {
        switch (cell.getType()) {
            case Empty: {
                if(!cell.isRevealed() || !cell.isFlagged() || !cell.isExploded()) {  
                    setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_0);
                }
                break;
            }
            case Number: {
                if(!cell.isRevealed() || !cell.isFlagged() || !cell.isExploded()) {
                    switch(cell.number) {
                        case 1: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_1); 
                            break;
                        }
                        case 2: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_2);
                            break;
                        }
                        case 3: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_3);
                            break;
                        }
                        case 4: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_4);
                            break;
                        }
                        case 5: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_5);
                            break;
                        }
                        case 6: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_6);
                            break;
                        }
                        case 7: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_7);
                            break;
                        }
                        case 8: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_8);
                            break;
                        }
                        default: {
                            break;
                        }
                    }
                }
                break;
            }
            case Mine: {
                if(cell.isExploded()) {
                    setDiplayBlock(x, y, MinecleanerHeads.EXPLODED);
                } else {
                    setDiplayBlock(x, y, MinecleanerHeads.TNT);
                }
                break;
            }
            default: {
                // Invalid
                break;
            }
        }

    }

    public List<Location> getBlocks() {
        ArrayList<Location> blocks = new ArrayList<>();
        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;

        Location loc = location.clone();

        for(int fx = -1 - widthIndex; fx < 2; fx++) {
            for(int fy = -1; fy < 2 + widthIndex; fy++) {
                loc.set(location.getX() + d1x * fx, location.getY() + fy, location.getZ() + d1z * fx);
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
        // todo: larger for bigger boards
        switch (widthIndex) {
            case 0:
                return dist > 64.0;
            case 1:
                return dist > 96.0;
            case 2:
                return dist > 128.0;
        }
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

    public int getSize() {
        return BoardSize.boardSizes[widthIndex];
    }

    // Difficulty
    public int getWidthIndex() {
        return widthIndex;
    }
    
}
