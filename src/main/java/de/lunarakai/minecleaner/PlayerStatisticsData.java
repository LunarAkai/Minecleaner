package de.lunarakai.minecleaner;

import java.util.UUID;

public class PlayerStatisticsData {
    private UUID playerUUID;
    private String playerName;
    private int gamesPlayed;
    private int gamesPlayedThisMonth;

    public PlayerStatisticsData(UUID playerUUID, String playerName, int gamesPlayed, int gamesPlayedThisMonth) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;
        this.gamesPlayed = gamesPlayed;
        this.gamesPlayedThisMonth = gamesPlayedThisMonth;
    }

    public UUID getPlayerID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }

    public int getGamesPlayedThisMonth() {
        return gamesPlayedThisMonth;
    }
    
}
