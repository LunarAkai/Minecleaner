package de.lunarakai.minecleaner.game;

import org.joml.Vector3i;

public class Cell {
    public enum CellType {
        Invalid,
        Empty,
        Mine,
        Number,
    }


    public Vector3i position;
    public CellType type;
    public int number;
    public boolean revealed;
    public boolean flagged;
    public boolean exploded;

    public void setType(CellType type) {
        this.type = type;
    }

    public CellType getType() {
        return type;
    }

}
