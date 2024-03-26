package de.lunarakai.minecleaner;

import java.util.HashMap;
import java.util.UUID;

public class PlayerStatisticsData {
    private UUID playerUUID;
    private String playerName;
    private HashMap<Integer, Integer> totalGamesPlayedSize;
    private HashMap<Integer, Integer> totalGamesPlayedSizeThisMonth;
    private int gamesPlayed;
    private int gamesPlayedThisMonth;
    private HashMap<Integer, Integer> gamesPlayedSize;
    private HashMap<Integer, Integer> gamesPlayedSizeThisMonth;
    private int pointsAcquiredTotal;
    private int pointsAcquiredMonth;
    private HashMap<Integer, Integer> bestTime;
    private HashMap<Integer, Integer> bestTimeThisMonth;

    public PlayerStatisticsData(UUID playerUUID, String playerName, 
        HashMap<Integer, Integer> totalGamesPlayedSize, 
        HashMap<Integer, Integer> totalGamesPlayedSizeThisMonth, 
        int gamesPlayed, 
        int gamesPlayedThisMonth,
        HashMap<Integer, Integer> gamesPlayedSize, 
        HashMap<Integer, Integer> gamesPlayedSizeThisMonth, 
        int pointsAcquiredTotal, 
        int pointsAcquiredMonth,
        HashMap<Integer, Integer> bestTime,
        HashMap<Integer, Integer> bestTimeThisMonth) {
        this.playerUUID = playerUUID;
        this.playerName = playerName;

        this.totalGamesPlayedSize = totalGamesPlayedSize;
        this.totalGamesPlayedSizeThisMonth = totalGamesPlayedSizeThisMonth;

        this.gamesPlayed = gamesPlayed;
        this.gamesPlayedThisMonth = gamesPlayedThisMonth;
        
        this.gamesPlayedSize = gamesPlayedSize;
        this.gamesPlayedSizeThisMonth = gamesPlayedSizeThisMonth;
        
        this.pointsAcquiredTotal = pointsAcquiredTotal;
        this.pointsAcquiredMonth = pointsAcquiredMonth;

        this.bestTime = bestTime;
        this.bestTimeThisMonth = bestTimeThisMonth;
    }

    public UUID getPlayerID() {
        return playerUUID;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getTotalGamesPlayedSize(Integer widthIndex) {
        Integer value = totalGamesPlayedSize.get(widthIndex);
        return value == null ? 0 : value;
    }

    public int getTotalGamesPlayedSizeThisMonth(Integer widthIndex) {
        Integer value = totalGamesPlayedSizeThisMonth.get(widthIndex);
        return value == null ? 0 : value;
    }

    public int getWonGamesPlayed() {
        return gamesPlayed;
    }

    public int getWonGamesPlayedThisMonth() {
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

    public Integer getBestTime(Integer widthIndex) {
        return bestTime.get(widthIndex);
    }

    public Integer getBestTimeThisMonth(Integer widthIndex) {
        return bestTimeThisMonth.get(widthIndex);
    }
}
