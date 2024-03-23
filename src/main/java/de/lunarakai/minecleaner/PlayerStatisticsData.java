package de.lunarakai.minecleaner;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatisticsData {
    private UUID playerUUID;
    private String playerName;
    private int gamesPlayed;
    private int gamesPlayedThisMonth;
    private HashMap<Integer, Integer> gamesPlayedSize;
    private HashMap<Integer, Integer> gamesPlayedSizeThisMonth;
    private int pointsAcquiredTotal;
    private int pointsAcquiredMonth;

    public PlayerStatisticsData(UUID playerUUID, String playerName, int gamesPlayed, int gamesPlayedThisMonth,
        HashMap<Integer, Integer> gamesPlayedSize, HashMap<Integer, Integer> gamesPlayedSizeThisMonth, 
        int pointsAcquiredTotal, int pointsAcquiredMonth) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;

        this.gamesPlayed = gamesPlayed;
        this.gamesPlayedThisMonth = gamesPlayedThisMonth;
        
        this.gamesPlayedSize = gamesPlayedSize;
        this.gamesPlayedSizeThisMonth = gamesPlayedSizeThisMonth;
        
        this.pointsAcquiredTotal = pointsAcquiredTotal;
        this.pointsAcquiredMonth = pointsAcquiredMonth;
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

    public int getGamesPlayedSize(Integer widthIndex) {
        Integer value = gamesPlayedSize.get(widthIndex);
        return value == null ? 0 : value;
    }

    public int getGamesPlayedSizeThisMonth(Integer widthIndex) {
        Integer value = gamesPlayedSizeThisMonth.get(widthIndex);
        return value == null ? 0 : value;
    }

    public int getPointsAcquiredTotal() {
        return pointsAcquiredTotal;
    }
    
    public int getPointsAquiredMonth() {
        return pointsAcquiredMonth;
    }
    
}
