package de.lunarakai.minecleaner.game;

import org.joml.Vector3i;
import de.lunarakai.minecleaner.utils.MathUtils;

public class Game {

    public int width;
    public int height;
    private int mineCount = 10;
    private Cell[][] state;
    private boolean gameover;
    private Board board;
    private BoardSize boardSize;
    private Tilemap tilemap;

    private void onValidate() {
        mineCount = MathUtils.clamp(mineCount, 0, width*height);
    }

    public void start() {
        board = new Board();

        int[] _boardSizes = boardSize.boardSizes;
        int[] _mineCounter = boardSize.mineCounter;

        int _boardSizeIndex = 0;
        width = _boardSizes[_boardSizeIndex];
        height = _boardSizes[_boardSizeIndex];
        mineCount = _mineCounter[_boardSizeIndex];

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
                cell.position = new Vector3i(x, 0, y);
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

    public boolean flag(int x, int y) {
        // TODO: Vector3 worldPosition = Camera.main.ScreenToWorldPoint(Input.mousePosition); <- Unity
        //Vector3i cellPosition = null; // TODO board.tilemap.WorldToCell(worldPosition); <- Unity
        Cell cell = getCell(x, y);

        if (cell.getType() == Cell.CellType.Invalid || cell.revealed) {
            return false;
        }

        boolean isFlaggedAlready = false;
        if(cell.flagged) {
            isFlaggedAlready = true;
        }

        cell.flagged = !cell.flagged;
        state[x][y] = cell;
        board.draw(state, tilemap);
        return isFlaggedAlready;
    }

    public boolean reveal(int x, int y) {
        // TODO: Vector3 worldPosition = Camera.main.ScreenToWorldPoint(Input.mousePosition); <- Unity
        //Vector3i cellPosition = null; // TODO board.tilemap.WorldToCell(worldPosition); <- Unity
        Cell cell = getCell(x, y);

        if(cell.getType() == Cell.CellType.Invalid || cell.revealed || cell.flagged) {
            return false;
        }

        boolean hitMine = false;

        switch (cell.getType()) {
            case Mine:
                explode(cell);
                hitMine = true;
                break;
            case Empty:
                flood(cell);
                checkWinCondition();
                break;
            default:
                cell.revealed = true;
                state[x][y] = cell;
                checkWinCondition();
                break;
        }
        board.draw(state, tilemap);
        return hitMine;
    }

    public void flood(Cell cell) {
        if(cell.revealed) return;
        if(cell.getType() == Cell.CellType.Mine || cell.getType() == Cell.CellType.Invalid) return;

        cell.revealed = true;
        state[cell.position.x][cell.position.z] = cell;

        if(cell.getType() == Cell.CellType.Empty) {
            flood(getCell(cell.position.x -1, cell.position.z));
            flood(getCell(cell.position.x +1, cell.position.z));
            flood(getCell(cell.position.x, cell.position.z -1));
            flood(getCell(cell.position.x, cell.position.z +1));
        }

        // TODO return cellpos of flooded cell to update the block displays
    }

    private void explode(Cell cell) {
        gameover = true;
        cell.revealed = true;
        cell.exploded = true;
        state[cell.position.x][cell.position.z] = cell;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                cell = state[x][y];
                if(cell.getType() == Cell.CellType.Mine) {
                    cell.revealed = true;
                    state[x][y] = cell;
                }
            }
        }
    }

    private void checkWinCondition() {
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

    private Cell getCell(int x, int y) {
        if(isValid(x,y)) {
            return state[x][y];
        } else {
            return new Cell();
        }
    }

    private boolean isValid(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
}
