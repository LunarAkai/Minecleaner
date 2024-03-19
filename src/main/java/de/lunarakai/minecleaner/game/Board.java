package de.lunarakai.minecleaner.game;

public class Board {

    public Tilemap tilemap;

    public void draw(Cell[][] state) {
        tilemap.clearAllTiles();

        int width = state[0].length;
        int height = state[1].length;

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                Cell cell = state[x][y];
                tilemap.setTile(cell.position, getTile(cell));
            }
        }
    }

    private Tile.TileType getTile(Cell cell) {
        if(cell.revealed) {
            return getRevealedTile(cell);
        } else if (cell.flagged) {
            return Tile.TileType.TileFlag;
        } else {
            return Tile.TileType.TileUnknown;
        }
    }

    private Tile.TileType getRevealedTile(Cell cell) {
        switch (cell.type) {
            case Empty: return Tile.TileType.TileEmpty;
            case Mine: return cell.exploded ? Tile.TileType.TileExploded : Tile.TileType.TileMine;
            case Number: return getNumberTile(cell);
            default: return null;
        }
    }

    private Tile.TileType getNumberTile(Cell cell) {
        switch (cell.number) {
            case 1: return Tile.TileType.TileNum1;
            case 2: return Tile.TileType.TileNum2;
            case 3: return Tile.TileType.TileNum3;
            case 4: return Tile.TileType.TileNum4;
            case 5: return Tile.TileType.TileNum5;
            case 6: return Tile.TileType.TileNum6;
            case 7: return Tile.TileType.TileNum7;
            case 8: return Tile.TileType.TileNum8;
            default: return null;
        }
    }

}
