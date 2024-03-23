package de.lunarakai.minecleaner.game;

import java.util.ArrayList;
import org.joml.Vector2i;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import de.lunarakai.minecleaner.utils.MathUtils;

public class Game {
    public boolean gameover;

    private MinecleanerPlugin plugin; 
    private int width;
    private int height;
    private int mineCount;

    private Cell[][] state;
    private Board board;
    private BoardSize boardSize;
    private Tilemap tilemap;

    private ArrayList<Cell> floodedCells;
    private ArrayList<Cell> explodedCells;

    public Game(MinecleanerPlugin plugin, int width, int mineCount) {
        this.plugin = plugin;
        this.width = width;
        this.height = width;
        this.mineCount = mineCount;

        this.floodedCells = new ArrayList<>();
        this.explodedCells = new ArrayList<>();
    }

    private void onValidate() {
        mineCount = MathUtils.clamp(mineCount, 0, width*height);
    }

    public void start() {
        board = new Board();
        newGame();
    }

    private void newGame() {
        state = new Cell[width][height];
        Tile[][] tile = new Tile[width][height];

        tilemap = new Tilemap(tile);

        gameover = false;

        generateCells();
        generateMines();
        generateNumbers();

        board.draw(state, tilemap);
    }

    private void generateCells() {
        for (int x = 0; x < width; x ++) {
            for (int y = 0; y < height; y++) {
                Cell cell = new Cell();
                cell.position = new Vector2i(x, y);
                cell.setType(Cell.CellType.Empty);
                state[x][y] = cell;
            }
        }
    }

    private void generateMines() {
        for (int i = 0; i < mineCount; i++) {
            int x = (int) (Math.random() * width);
            int y = (int) (Math.random() * height);

            while (state[x][y].type == Cell.CellType.Mine) {
                x++;

                if(x >= width) {
                    x = 0;
                    y++;

                    if(y >= height) {
                        y=0;
                    }
                }
            }
            state[x][y].setType(Cell.CellType.Mine);
        }
    }

    private void generateNumbers() {
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Cell cell = state[x][y];

                if(cell.getType() == Cell.CellType.Mine) {
                    continue;
                }

                cell.number = countMines(x, y);

                if(cell.number > 0) {
                    cell.setType(Cell.CellType.Number);
                }

                state[x][y] = cell;
            }
        }
    }

    public Tilemap getMinecleanerTilemap() {
        return tilemap;
    }

    private int countMines(int cellX, int cellY) {
        int count = 0;

        for (int adjacentX = -1; adjacentX <= 1; adjacentX++){
            for (int adjacentY = -1; adjacentY <= 1; adjacentY++) {
                if(adjacentX == 0 && adjacentY == 0) {
                    continue;
                }

                int x = cellX + adjacentX;
                int y = cellY + adjacentY;

                if (getCell(x,y).getType() == Cell.CellType.Mine) {
                    count++;
                }
            }
        }
        return count;
    }

    public void flag(int x, int y) {
        Cell cell = getCell(x, y);

        if (cell.getType() == Cell.CellType.Invalid || cell.isRevealed()) {
            return;
        }

        cell.flagged = !cell.isFlagged();
        state[x][y] = cell;
        board.draw(state, tilemap);
    }

    public void reveal(int x, int y) {
        Cell cell = getCell(x, y);

        if(cell.getType() == Cell.CellType.Invalid || cell.isRevealed() || cell.flagged) {
            return;
        }

        //boolean hitMine = false;

        switch (cell.getType()) {
            case Mine: {
                explode(cell);
                break;
            }
            case Empty: {
                if(!floodedCells.isEmpty()) {;
                    floodedCells.clear();
                }
                flood(cell);
                checkWinCondition();
                break;
            }
            default: {
                cell.setRevealed();
                state[x][y] = cell;
                checkWinCondition();
                break;
            }
                
        }
        board.draw(state, tilemap);
    }

    public void flood(Cell cell) {
        if(cell.isRevealed()) return;
        if(cell.getType() == Cell.CellType.Mine || cell.getType() == Cell.CellType.Invalid || cell.position == null) return;

        if(cell.isFlagged()) {
            cell.setFlaggedState(false);
        }
        
        cell.setRevealed();
        floodedCells.add(cell);
        state[cell.position.x][cell.position.y] = cell;    

        // this looks bad, i should probably go to the doctor with it D: 
        if(cell.getType() == Cell.CellType.Empty) {
            if(isValid(cell.position.x -1, cell.position.y)) {
                flood(getCell(cell.position.x -1, cell.position.y));
            }
            if(isValid(cell.position.x +1, cell.position.y)) {
                flood(getCell(cell.position.x +1, cell.position.y));

            }
            if(isValid(cell.position.x, cell.position.y -1)) {
                flood(getCell(cell.position.x, cell.position.y -1));
            }
            if(isValid(cell.position.x, cell.position.y +1)) {
                flood(getCell(cell.position.x, cell.position.y +1));
            }

            // Corners
            // Todo: oben links geht nicht
            if(isValid(cell.position.x + 1, cell.position.y +1)) {
                flood(getCell(cell.position.x +1, cell.position.y +1));
            }
            if(isValid(cell.position.x + 1, cell.position.y -1)) {
                flood(getCell(cell.position.x +1, cell.position.y -1));
            }
            if(isValid(cell.position.x - 1, cell.position.y +1)) {
                flood(getCell(cell.position.x -1, cell.position.y +1));
            }
            if(isValid(cell.position.x - 1, cell.position.y - 1)) {
                flood(getCell(cell.position.x -1, cell.position.y -1));
            }
        }
    }

    private void explode(Cell cell) {
        gameover = true;
        cell.revealed = true;
        cell.exploded = true;
        state[cell.position.x][cell.position.y] = cell;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cell = state[x][y];
                if(cell.getType() == Cell.CellType.Mine) {
                    cell.revealed = true;
                    state[x][y] = cell;
                    explodedCells.add(cell);
                }
            }
        }
    }

    public void checkWinCondition() {
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = state[x][y];
                if(cell.getType() != Cell.CellType.Mine && !cell.revealed) {
                    return;
                }
            }
        }

        gameover = true;

        for(int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = state[x][y];

                if(cell.getType() == Cell.CellType.Mine) {
                    cell.flagged = true;
                    state[x][y] = cell;
                }
            }
        }
    }

    public Cell getCell(int x, int y) {
        if(isValid(x,y)) {
            return state[x][y];
        } else {
            return new Cell();
        }
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    public ArrayList<Cell> getfloodedCells() {
        return floodedCells;
    }

    public ArrayList<Cell> getExplodedCells() {
        return explodedCells;
    }
}
