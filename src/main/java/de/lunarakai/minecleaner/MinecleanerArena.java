package de.lunarakai.minecleaner;

import com.google.common.base.Preconditions;
import de.lunarakai.minecleaner.game.BoardSize;
import de.lunarakai.minecleaner.game.Cell;
import de.lunarakai.minecleaner.game.Game;
import de.lunarakai.minecleaner.utils.MinecleanerHeads;
import de.lunarakai.minecleaner.utils.MinecleanerStringUtil;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Display;
import org.bukkit.entity.Display.Brightness;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemDisplay;
import org.bukkit.entity.Player;
import org.bukkit.entity.TextDisplay;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Transformation;
import org.joml.Vector2i;
import org.joml.Vector3f;

public class MinecleanerArena {
    private final MinecleanerPlugin plugin;
    private final String name;
    private final Location location;
    private final Location centerLocation;
    private int widthIndex = 0;
    private final BlockFace orientation;
    private ArenaStatus arenaStatus = ArenaStatus.INACTIVE;
    private UUID[] blockDisplays;
    private TextDisplay textDisplay;
    private boolean hasMadeFirstClick = false;
    private int flagsPlaced = 0;
    private Player currentPlayer;
    private long currentGameStartTime;
    private long ingameTime;
    private Game currentMinecleanerGame;
    private final Location tempLoc = new Location(null, 0, 0, 0);

    public MinecleanerArena(MinecleanerPlugin plugin, ConfigurationSection arenaSection) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(arenaSection.getString("name"));
        this.location = Preconditions.checkNotNull(arenaSection.getLocation("location"));
        this.widthIndex = Preconditions.checkNotNull(arenaSection.getInt("fieldwidth"));
        this.blockDisplays = new UUID[BoardSize.boardSizesWidth[widthIndex] * BoardSize.boardSizesHeight[widthIndex]];

        BlockFace orientation = BlockFace.NORTH;
        try {
            orientation = BlockFace.valueOf(arenaSection.getString("orientation"));
        } catch (IllegalArgumentException ignored) {

        }
        this.orientation = orientation;

        this.centerLocation = location.clone().add(0.5, 0, 0.5);

        List<String> list = arenaSection.getStringList("blockdisplays");
        for (int i = 0; i < list.size(); i++) {
            String blockDisplay = list.get(i);
            if (blockDisplay != null) {
                blockDisplays[i] = UUID.fromString(blockDisplay);
            }
        }
    }

    public MinecleanerArena(MinecleanerPlugin plugin, String name, Location location, int widthIndex, BlockFace orientation) {
        this.plugin = plugin;
        this.name = Preconditions.checkNotNull(name, "name");
        this.location = Preconditions.checkNotNull(location, "location");
        this.widthIndex = Preconditions.checkNotNull(widthIndex, ("fieldwidth"));
        this.blockDisplays = new UUID[BoardSize.boardSizesWidth[widthIndex] * BoardSize.boardSizesHeight[widthIndex]];

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

        for (int fx = -1 - (BoardSize.boardSizesWidth[widthIndex] / 3 - 3); fx < 2; fx++) {
            for (int fy = -1; fy < BoardSize.boardSizesHeight[widthIndex] / 3 - 1; fy++) {
                loc.set(location.getX() + d1x * fx, location.getY() + fy, location.getZ() + d1z * fx);
                boolean f = (fx + fy) % 2 == 0;
                world.setBlockData(loc, f ? block0 : block1);
            }
        }
    }

    /*
     *  "Mitte" = Block (1,1)
     */
    public void generateBlockDisplays() {
        int sizeWidth = BoardSize.boardSizesWidth[widthIndex];
        int sizeHeight = BoardSize.boardSizesHeight[widthIndex];

        World world = location.getWorld();
        for (UUID id : blockDisplays) {
            if (id != null) {
                Entity blockdisplay = world.getEntity(id);
                if (blockdisplay instanceof Display) {
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

        if (orientation == BlockFace.EAST) {
            eastWestGapFixX = 0.5;
            eastWestGapFixZ = -0.55;
        } else if (orientation == BlockFace.SOUTH) {
            southGapFixX = 1.02;
            southGapFixZ = -0.05;
        } else if (orientation == BlockFace.WEST) {
            eastWestGapFixX = 0.55;
            eastWestGapFixZ = 0.5;
        }

        rotation0 = getRotationYaw();
        float rotation = rotation0;

        int d0x = orientation.getModX();
        int d0z = orientation.getModZ();
        int d1x = -d0z;
        int d1z = d0x;

        Location loc = location.clone();

        for (int fx = 0; fx < sizeHeight; fx++) {
            final int fxf = fx;
            for (int fz = 0; fz < sizeWidth; fz++) {
                final int fzf = fz;

                loc.set(location.getX() - 0.016 + eastWestGapFixX + southGapFixX - (d1x * fz) / 3.0 + d0x * 0.55 + d1x * 1.847,
                        location.getY() - 0.8225 + fxf / 3.0,
                        location.getZ() + 0.525 + eastWestGapFixZ + southGapFixZ - (d1z * fz) / 3.0 + d0z * 0.55 + d1z * 1.847);

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
                    blockdisplay.setBrightness(new Brightness(15, 15));

                    blockdisplay.setItemStack(MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN.getHead());
                });

                if (blockDisplay != null) {
                    blockDisplays[fxf * sizeWidth + fzf] = blockDisplay.getUniqueId();
                }
            }
        }
        showStartHeads();
    }

    public void save(ConfigurationSection arenaSection) {
        arenaSection.set("name", this.name);
        arenaSection.set("location", this.location);
        arenaSection.set("fieldwidth", this.widthIndex);
        arenaSection.set("orientation", this.orientation.name());
        List<String> blockDisplays = new ArrayList<>();
        for (UUID uuid : this.blockDisplays) {
            blockDisplays.add(uuid == null ? null : uuid.toString());
        }
        arenaSection.set("blockdisplays", blockDisplays);
    }

    private void setDiplayBlock(int x, int y, MinecleanerHeads head, boolean applyUsualRotation) {
        int sizeWidth = BoardSize.boardSizesWidth[widthIndex];
        int sizeHeight = BoardSize.boardSizesHeight[widthIndex];

        UUID blockDisplayId = blockDisplays[x + y * sizeWidth];
        Entity blockDisplay = blockDisplayId != null ? location.getWorld().getEntity(blockDisplayId) : null;
        if (blockDisplay instanceof ItemDisplay) {
            ItemDisplay display = (ItemDisplay) blockDisplay;
            if (!applyUsualRotation) {
                blockDisplay.setRotation(blockDisplay.getYaw(), 0);
            } else {
                blockDisplay.setRotation(blockDisplay.getYaw(), -90);
            }
            display.setItemStack(head.getHead());
        }
    }

    public void startNewGame() {
        currentMinecleanerGame = new Game(plugin, BoardSize.boardSizesWidth[widthIndex], BoardSize.boardSizesHeight[widthIndex], BoardSize.mineCounter[widthIndex]);
        currentMinecleanerGame.start();
        showTextDisplay();

        removeStartHeads();
        ingameTime = 0;
        flagsPlaced = 0;
        hasMadeFirstClick = false;
        arenaStatus = ArenaStatus.PLAYING;
        currentGameStartTime = System.currentTimeMillis();

        new BukkitRunnable() {
            @Override
                public void run() {
                if(arenaStatus == ArenaStatus.PLAYING && currentPlayer != null) {
                    if(!currentMinecleanerGame.gameover) {
                        ingameTime++;
                    }
                    if(plugin.isStatisticsEnabled()) {
                        if(plugin.getManager().getSettingsValue("additionaldisplay", currentPlayer) != 0
                                || plugin.getManager().getSettingsValue("timer", currentPlayer) != 0) {
                            updateIngameInfoTexts();
                        }
                    }

                } else {
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 1L, 1L);

    }

    public void addJoiningPlayer(Player player) {
        Preconditions.checkNotNull(player);
        Preconditions.checkState(arenaStatus == ArenaStatus.INACTIVE);
        this.arenaStatus = ArenaStatus.CONFIRM_PLAYING;
        this.currentPlayer = player;
    }

    public void removePlayer() {
        int sizeWidth = BoardSize.boardSizesWidth[widthIndex];
        int sizeHeight = BoardSize.boardSizesHeight[widthIndex];

        this.arenaStatus = ArenaStatus.INACTIVE;
        this.currentPlayer = null;
        this.currentMinecleanerGame = null;

        // load chunk of block -1 and x+1
//        loadBlockChunk();

        for (int x = 0; x < sizeWidth; x++) {
            for (int y = 0; y < sizeHeight; y++) {
                setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
            }
        }
        showStartHeads();
        removeTextDisplay();
    }

//    private void loadBlockChunk() {
//
//        BlockFace orientation = getOrientation();
//        Location loc1 = this.getLocation();
//        Location loc2 = this.getLocation();
//        double x;
//        double z;
//
//        switch (orientation) {
//            case NORTH:
//                // Block -1:
//                //  x: +1
//                x = loc1.x() + 3.0;
//                loc1 = new Location(loc1.getWorld(), x, loc1.y(), loc1.z());
//                // Block width+1:
//                //  x: -1;
//                x = loc2.x() - (double) getArenaWidth()/4 - 1;
//                loc2 = new Location(loc2.getWorld(), x, loc2.y(), loc2.z());
//                break;
//            case EAST:
//                // Block -1:
//                //  z: +1
//                z = loc1.z() + 3.0;
//                loc1 = new Location(loc2.getWorld(), loc2.x(), loc2.y(), z);
//                // Block width+1:
//                //  z: -1;
//                z = loc2.z() - (double) getArenaWidth()/4 - 1;
//                loc2 = new Location(loc2.getWorld(), loc2.x(), loc2.y(), z);
//                break;
//            case SOUTH:
//                // Block -1:
//                //  x: -1
//                x = loc1.x() - 3.0;
//                loc1 = new Location(loc1.getWorld(), x, loc1.y(), loc1.z());
//                // Block width+1:
//                //  x: +1;
//                x = loc2.x() + (double) getArenaWidth()/4 + 1;
//                loc2 = new Location(loc2.getWorld(), x, loc2.y(), loc2.z());
//                break;
//            case WEST:
//                // Block -1:
//                //  z: -1
//                z = loc1.z() - 3.0 ;
//                loc1 = new Location(loc2.getWorld(), loc2.x(), loc2.y(), z);
//                // Block width+1:
//                //  z: +1;
//                z = loc2.z() + (double) getArenaWidth()/4 - 1;
//                loc2 = new Location(loc2.getWorld(), loc2.x(), loc2.y(), z);
//                break;
//        }
//
//        plugin.getLogger().log(Level.WARNING, "Loc1: " + loc1);
//        plugin.getLogger().log(Level.WARNING, "Loc2: " + loc2);
//        loc1.getWorld().getChunkAt(loc1).load();
//        loc2.getWorld().getChunkAt(loc2).load();
//    }

    public void showStartHeads() {
        int width = BoardSize.boardSizesWidth[widthIndex];
        int height = BoardSize.boardSizesHeight[widthIndex];

        // MINE -
        setDiplayBlock(width / 2 - 2 + 0, height / 2 + 1, MinecleanerHeads.MINESWEEPER_LETTER_M, true);
        setDiplayBlock(width / 2 - 2 + 1, height / 2 + 1, MinecleanerHeads.MINESWEEPER_LETTER_I, true);
        setDiplayBlock(width / 2 - 2 + 2, height / 2 + 1, MinecleanerHeads.MINESWEEPER_LETTER_N, true);
        setDiplayBlock(width / 2 - 2 + 3, height / 2 + 1, MinecleanerHeads.MINESWEEPER_LETTER_E, true);
        setDiplayBlock(width / 2 - 2 + 4, height / 2 + 1, MinecleanerHeads.MINESWEEPER_LETTER_MINUS, true);

        // SWEEPER
        setDiplayBlock(width / 2 - 3 + 0, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_S, true);
        setDiplayBlock(width / 2 - 3 + 1, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_W, true);
        setDiplayBlock(width / 2 - 3 + 2, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_E, true);
        setDiplayBlock(width / 2 - 3 + 3, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_E, true);
        setDiplayBlock(width / 2 - 3 + 4, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_P, true);
        setDiplayBlock(width / 2 - 3 + 5, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_E, true);
        setDiplayBlock(width / 2 - 3 + 6, height / 2 - 1, MinecleanerHeads.MINESWEEPER_LETTER_R, true);

    }

    public void removeStartHeads() {
        int width = BoardSize.boardSizesWidth[widthIndex];
        int height = BoardSize.boardSizesHeight[widthIndex];

        // MINE -
        setDiplayBlock(width / 2 - 2 + 0, height / 2 + 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 2 + 1, height / 2 + 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 2 + 2, height / 2 + 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 2 + 3, height / 2 + 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 2 + 4, height / 2 + 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);

        // SWEEPER
        setDiplayBlock(width / 2 - 3 + 0, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 3 + 1, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 3 + 2, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 3 + 3, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 3 + 4, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 3 + 5, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
        setDiplayBlock(width / 2 - 3 + 6, height / 2 - 1, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
    }

    private void showTextDisplay() {
        Player player = this.getCurrentPlayer();
        World world = player.getWorld();

        double textCenterX = centerLocation.getX();
        double textCenterY = centerLocation.getY() + ((double) BoardSize.boardSizesHeight[widthIndex] / 3 - 2.75 - widthIndex);
        double textCenterZ = centerLocation.getZ();

        int rotation = getRotationYaw() - 180;

        switch (orientation) {
            case NORTH: {
                textCenterX = centerLocation.getX() - (((double) BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) + 2.51;
                textCenterZ = textCenterZ - 0.30;
                break;
            }
            case EAST: {
                textCenterX = textCenterX + 1.55;
                textCenterZ = centerLocation.getZ() - (((double) BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) + 1.775;
                break;
            }
            case SOUTH: {
                textCenterX = centerLocation.getX() + (((double) BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) - 0.5275;
                textCenterZ = textCenterZ + 0.80;
                break;
            }
            case WEST: {
                textCenterX = textCenterX + 0.45;
                textCenterZ = centerLocation.getZ() + (((double) BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) - 1.275;
                break;
            }
            default: {
                break;
            }
        }

        Location textDisplayLocation = new Location(player.getWorld(), textCenterX, textCenterY, textCenterZ);

        textDisplay = world.spawn(textDisplayLocation.add(-1, 2 + widthIndex, -0.25), TextDisplay.class, textdisplay -> {
            Transformation transformation = textdisplay.getTransformation();
            Vector3f newTranslationScale = new Vector3f(1.0f, 1.0f, 1.0f);
            Transformation newTransformation;
            newTransformation = new Transformation(
                    transformation.getTranslation(),
                    transformation.getLeftRotation(),
                    newTranslationScale,
                    transformation.getRightRotation());

            textdisplay.setTransformation(newTransformation);
            textdisplay.setRotation(rotation, 0);

            textdisplay.setBillboard(Display.Billboard.FIXED);
            textdisplay.setBrightness(new Brightness(15, 15));
            textdisplay.setVisibleByDefault(true);
            textdisplay.setDisplayHeight(3);
            textdisplay.setDisplayWidth((float) BoardSize.boardSizesWidth[widthIndex] / 3);
            textdisplay.setPersistent(false);
            textdisplay.text(Component.text(ChatColor.GOLD + plugin.getDisplayedPluginName()));
        });
    }

    public void updateIngameInfoTexts() {
        String timer = "";
        if(plugin.isStatisticsEnabled()) {
            if(plugin.getManager().getSettingsValue("timer", currentPlayer) != 0) {
                timer = ChatColor.GOLD + " Zeit: " + MinecleanerStringUtil.timeToString((ingameTime/20)*1000, true)  + " ";
            }
            if(plugin.getManager().getSettingsValue("additionaldisplay", currentPlayer) != 0 && plugin.isStatisticsEnabled()) {
                String componentActionBar = ChatColor.GREEN + "Flaggen gesetzt: " + flagsPlaced + ChatColor.RED + "  Minen insgesamt: " + BoardSize.mineCounter[widthIndex];
                currentPlayer.sendActionBar(Component.text(componentActionBar + " " + timer));
            }
        }

        if (textDisplay != null) {
            String component = ChatColor.GREEN + "-- Flaggen gesetzt: " + flagsPlaced + " --" + "\n" + ChatColor.RED + "-- Minen insgesamt: " + BoardSize.mineCounter[widthIndex] + " --";
            //textDisplay.text(Component.text(ChatColor.GREEN + "-- Flaggen gesetzt: " + flagsPlaced + " --" + "\n" + ChatColor.RED + "-- Minen insgesamt: " + BoardSize.mineCounter[widthIndex] + " --"));
            String newLine = "";
            String filler = "";
            if(!timer.equals("")) {
                newLine = "\n" + ChatColor.GOLD + "-- ";
                filler = " --";
            }
            textDisplay.text(Component.text(component + newLine + timer + filler));
        }


    }

    public void removeTextDisplay() {
        if (textDisplay != null) {
            textDisplay.remove();
        }
    }

    public void removeBlockDisplays() {
        int sizeWidth = BoardSize.boardSizesWidth[widthIndex];
        int sizeHeight = BoardSize.boardSizesHeight[widthIndex];

        World world = location.getWorld();
        for (int fx = 0; fx < sizeWidth; fx++) {
            for (int fy = 0; fy < sizeHeight; fy++) {
                UUID blockDisplayUuid = blockDisplays[fx + fy * sizeWidth];
                Entity blockDisplayEntity = blockDisplayUuid != null ? world.getEntity(blockDisplayUuid) : null;
                if (blockDisplayEntity instanceof Display blockdisplay) {
                    blockDisplayEntity.remove();
                }
            }
        }
    }

    public void flagCell(int x, int y) {
        if (currentMinecleanerGame != null && !currentMinecleanerGame.gameover) {
            Cell cell = currentMinecleanerGame.getCell(x, y);
            if (!cell.isRevealed()) {
                Player player = this.currentPlayer;

                currentMinecleanerGame.flag(x, y);
                if (currentMinecleanerGame.gameover) {
                    plugin.getManager().handleGameover(player, this, true);
                }
                if (cell.isFlagged() == true) {
                    flagsPlaced = flagsPlaced + 1;
                    updateIngameInfoTexts();
                    setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_FLAG, true);
                }
                if (cell.isFlagged() == false) {
                    flagsPlaced = flagsPlaced - 1;
                    updateIngameInfoTexts();
                    setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_UNKNOWN, true);
                }
            }
        }
    }

    public void revealCell(int x, int y) {
        if (currentMinecleanerGame != null && !currentMinecleanerGame.gameover) {
            Cell cell = currentMinecleanerGame.getCell(x, y);
            if (!cell.isFlagged()) {
                Player player = this.currentPlayer;

                if (!hasMadeFirstClick) {
                    currentMinecleanerGame.firstClick(x, y);
                    hasMadeFirstClick = true;
                }

                currentMinecleanerGame.reveal(x, y);
                setBlockForCellType(x, y, cell);

                if (currentMinecleanerGame.gameover) {
                    plugin.getManager().handleGameover(player, this, !(cell.isRevealed() && cell.isExploded()));
                } else {
                    updateIngameInfoTexts();
                }

                ArrayList<Cell> floodedCells = currentMinecleanerGame.getfloodedCells();
                if (floodedCells != null) {
                    for (int i = 0; i < floodedCells.size(); i++) {
                        Vector2i pos = floodedCells.get(i).position;
                        setBlockForCellType(pos.x, pos.y, floodedCells.get(i));
                    }
                    flagsPlaced = flagsPlaced - currentMinecleanerGame.getFloodedFlaggedCells();
                }
            }
        }
    }

    public void showMines() {
        ArrayList<Cell> explodedCells = currentMinecleanerGame.getExplodedCells();
        if (explodedCells != null) {
            for (int i = 0; i < explodedCells.size(); i++) {
                Vector2i pos = explodedCells.get(i).position;
                setBlockForCellType(pos.x, pos.y, explodedCells.get(i));
            }
        }
    }

    private void setBlockForCellType(int x, int y, Cell cell) {
        if(cell.getType() == null) return;
        switch (cell.getType()) {
            case Empty: {
                if (!cell.isRevealed() || !cell.isFlagged() || !cell.isExploded()) {
                    setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_0, true);
                }
                break;
            }
            case Number: {
                if (!cell.isRevealed() || !cell.isFlagged() || !cell.isExploded()) {
                    switch (cell.number) {
                        case 1: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_1, true);
                            break;
                        }
                        case 2: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_2, true);
                            break;
                        }
                        case 3: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_3, true);
                            break;
                        }
                        case 4: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_4, true);
                            break;
                        }
                        case 5: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_5, true);
                            break;
                        }
                        case 6: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_6, true);
                            break;
                        }
                        case 7: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_7, true);
                            break;
                        }
                        case 8: {
                            setDiplayBlock(x, y, MinecleanerHeads.MINESWEEPER_TILE_8, true);
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
                if (cell.isExploded()) {
                    setDiplayBlock(x, y, MinecleanerHeads.EXPLODED, true);
                } else {
                    setDiplayBlock(x, y, MinecleanerHeads.TNT, true);
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

        for (int fx = -1 - (BoardSize.boardSizesWidth[widthIndex] / 3 - 3); fx < 2; fx++) { // boardWith/3
            for (int fy = -1; fy < BoardSize.boardSizesHeight[widthIndex] / 3 - 1; fy++) { // fy < boardHeight/3 - 1
                loc.set(location.getX() + d1x * fx, location.getY() + fy, location.getZ() + d1z * fx);
                blocks.add(loc.clone());
            }
        }
        return blocks;
    }

    public boolean isTooFarAway(Player player) {
        if (currentMinecleanerGame != null && currentMinecleanerGame.gameover) return false;
        if (player.getWorld() != location.getWorld()) {
            return true;
        }
        player.getLocation(tempLoc);

        double centerX = centerLocation.getX();
        double centerY = centerLocation.getY() + (BoardSize.boardSizesHeight[widthIndex] / 3) / 2.0 - 1;
        double centerZ = centerLocation.getZ();

        switch (orientation) {
            case NORTH: {
                centerX = centerLocation.getX() - ((BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) + 1;
                break;
            }
            case EAST: {
                centerZ = centerLocation.getZ() - ((BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) + 1;
                break;
            }
            case SOUTH: {
                centerX = centerLocation.getX() + ((BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) - 1;
                break;
            }
            case WEST: {
                centerZ = centerLocation.getZ() + ((BoardSize.boardSizesWidth[widthIndex] / 3) / 2.0) - 1;
                break;
            }
            default: {
                break;
            }
        }

        Location trueCenterLocation = new Location(player.getWorld(), centerX, centerY, centerZ);
        double dist = tempLoc.distanceSquared(trueCenterLocation);

        return dist > Math.pow((BoardSize.boardSizesWidth[widthIndex] / 4.5) + 6, 2);
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

    public int getArenaWidth() {
        return BoardSize.boardSizesWidth[widthIndex];
    }

    public int getArenaHeight() { return BoardSize.boardSizesHeight[widthIndex]; }

    public long getCurrentGameStartTime() {
        return currentGameStartTime;
    }

    public int getWidthIndex() {
        return widthIndex;
    }

    private int getRotationYaw() {
        return switch (orientation) {
            case EAST -> 90;
            case SOUTH -> 180;
            case WEST -> 270;
            default -> 0;
        };
    }
}