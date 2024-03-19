package de.lunarakai.minecleaner.game;

public class BoardSize {
    public int[] boardSizes = {
            8,
            16,
            32, // nicht größer als 24
            64
    };

    public int[] mineCounter = {
            10,
            40,
            128,
            512
    };
}
