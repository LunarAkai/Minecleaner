package de.lunarakai.minecleaner.game;

import org.joml.Vector2i;

public class Tilemap {

    private Tile[][] tiles;

    public Tilemap(Tile[][] tiles) {
        this.tiles = tiles;
    }

    public void clearAllTiles() {
        for(int i = 0; i < tiles.length; i++) {
            for(int j = 0; j < tiles[0].length; j++) {
                tiles[i][j] = new Tile();
            }
        }
    }

    public Tile getTile(int x, int y) {
        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
            return tiles[x][y];
        } else {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }
    
    public void setTile(Vector2i pos, Tile.TileType tileType) {
        int x = pos.x();
        int y = pos.y();

        if (x >= 0 && x < tiles.length && y >= 0 && y < tiles[0].length) {
            tiles[x][y].setTileType(tileType);
        } else {
            throw new IllegalArgumentException("Invalid coordinates");
        }
    }
}
