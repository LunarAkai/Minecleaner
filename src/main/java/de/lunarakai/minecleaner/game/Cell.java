package de.lunarakai.minecleaner.game;

import org.joml.Vector2i;

public class Cell {
    public enum CellType {
        Invalid,
        Empty,
        Mine,
        Number,
    }

    public Vector2i position;
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

    public void setRevealed() {
        this.revealed = true;
    }
    public boolean isRevealed() {
        return revealed;
    }

    public void setFlaggedState(boolean flag) {
        this.flagged = flag;
    }
    public boolean isFlagged() {
        return flagged;
    }

    public void setExploded() {
        this.exploded = true;
    }
    public boolean isExploded() {
        return exploded;
    }
}
