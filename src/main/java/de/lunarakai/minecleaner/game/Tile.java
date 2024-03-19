package de.lunarakai.minecleaner.game;

public class Tile {
    public enum TileType {
        TileUnknown,
        TileNum1,
        TileNum2,
        TileNum3,
        TileNum4,
        TileNum5,
        TileNum6,
        TileNum7,
        TileNum8,
        TileEmpty,
        TileMine,
        TileExploded,
        TileFlag,

    }

    public Tile.TileType tileType;

    public Tile() {
        setTileType(TileType.TileUnknown);
    }

    public void setTileType(Tile.TileType tileType) {
        this.tileType = tileType;
    }

    public Tile.TileType getTileType() {
        return tileType;
    }
}
