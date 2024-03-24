package de.lunarakai.minecleaner;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.google.common.base.Preconditions;
import de.iani.cubesidestats.api.PlayerStatistics;
import de.iani.cubesidestats.api.PlayerStatisticsQueryKey;
import de.iani.cubesidestats.api.PlayerStatisticsQueryKey.QueryType;
import de.iani.cubesidestats.api.StatisticKey;
import de.iani.cubesidestats.api.StatisticsQueryKey;
import de.iani.cubesidestats.api.TimeFrame;
import de.iani.cubesideutils.bukkit.items.ItemStacks;
import de.iani.playerUUIDCache.CachedPlayer;
import de.lunarakai.minecleaner.game.BoardSize;
import de.lunarakai.minecleaner.utils.MinecleanerStringUtil;
import net.md_5.bungee.api.ChatColor;

public class MinecleanerManager {
    private final MinecleanerPlugin plugin;
    private final Inventory confirmPlayingInventory;
    private final HashMap<Integer, String> sizes;

    // Statistics
    private final StatisticKey statisticsWonGamesTotal;
    private final StatisticKey statisticsPointsAcquired;
    private final HashMap<Integer, StatisticKey> statisticsGames;
    private final HashMap<Integer, StatisticKey> statisticsTimeRecord;
    private final HashMap<Integer, StatisticKey> statisticsTotalGamesPlayed;

    public MinecleanerManager(MinecleanerPlugin plugin) {
        this.plugin = plugin;
        
        this.sizes = new HashMap<>();
        this.sizes.put(0, "klein");
        this.sizes.put(1, "mittel");
        this.sizes.put(2, "groß");

        this.confirmPlayingInventory = plugin.getServer().createInventory(null, InventoryType.HOPPER, "Minecleaner starten?");
        this.confirmPlayingInventory.setItem(1, 
            ItemStacks.lore(ItemStacks.rename(new ItemStack(Material.GREEN_CONCRETE), ChatColor.GREEN + "Bestätigen")));
        this.confirmPlayingInventory.setItem(3,
            ItemStacks.lore(ItemStacks.rename(new ItemStack(Material.RED_CONCRETE), ChatColor.RED + "Abbrechen")));

        statisticsWonGamesTotal = plugin.getCubesideStatistics().getStatisticKey("minecleaner.wonGamestotal");
        statisticsWonGamesTotal.setIsMonthlyStats(true);
        statisticsWonGamesTotal.setDisplayName("Runden gewonnen");
        
        statisticsPointsAcquired = plugin.getCubesideStatistics().getStatisticKey("minecleaner.pointstotal");
        statisticsPointsAcquired.setIsMonthlyStats(true);
        statisticsPointsAcquired.setDisplayName("Punkte erspielt");

        statisticsGames = new HashMap<>();
        statisticsTimeRecord = new HashMap<>();
        statisticsTotalGamesPlayed = new HashMap<>();

        for(Entry<Integer, String> e : this.sizes.entrySet()) {
            String sizeDisplay = e.getValue();
            StatisticKey s = plugin.getCubesideStatistics().getStatisticKey("minecleaner.wongames.boardsize." + e.getKey());
            s.setIsMonthlyStats(true);
            s.setDisplayName("Runden gewonnen auf Spielfeldgröße " + sizeDisplay);
            statisticsGames.put(e.getKey(), s);

            s = plugin.getCubesideStatistics().getStatisticKey("minecleaner.gamestotal.boardsize." + e.getKey());
            s.setIsMonthlyStats(true);
            s.setDisplayName("Runden gespielt auf Spielfeldgröße " + sizeDisplay );
            statisticsTotalGamesPlayed.put(e.getKey(), s);

            s = plugin.getCubesideStatistics().getStatisticKey("minecleaner.timerecord." + e.getKey());
            s.setDisplayName("Bestzeit bei Größe " + sizeDisplay);
            statisticsTimeRecord.put(e.getKey(), s);
        }
    }

    public HashMap<Integer, String> getSizes() {
        return sizes;
    }
    
    public void joinArena(Player player, MinecleanerArena arena) {
        if (!player.hasPermission(MinecleanerPlugin.PERMISSION_PLAY)) {
            return;
        }
        Preconditions.checkArgument(plugin.getArenaList().getPlayerArena(player) == null, "player is in an arena");
        Preconditions.checkArgument(arena.getArenaStatus() == ArenaStatus.INACTIVE, "arena is in use");
        arena.addJoiningPlayer(player);
        plugin.getArenaList().setArenaForPlayer(player, arena);
        player.openInventory(confirmPlayingInventory);
    }

    public void leaveArena(Player player, boolean message) {
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
        Preconditions.checkArgument(arena != null, "player is in no arena");
        arena.removePlayer();
        plugin.getArenaList().setArenaForPlayer(player, null);
        if(message) {
            player.sendMessage(ChatColor.YELLOW + "Das Minecleanerspiel wurde abgebrochen.");
        }
    }

    public void startGame(Player player) {
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
        Preconditions.checkArgument(arena != null, "player is in no arena");
        Preconditions.checkState(arena.getArenaStatus() == ArenaStatus.CONFIRM_PLAYING, "not confirming playing status");
        arena.startNewGame();
        player.sendMessage(ChatColor.YELLOW + "Du hast eine neue Runde Minecleaner gestartet.");
    }

    public void handleGameover(Player player, MinecleanerArena arena, boolean isSuccessfullyCleared) {
        World world = player.getWorld();
        PlayerStatistics ps = plugin.getCubesideStatistics().getStatistics(player.getUniqueId());
        StatisticKey sg;

        if(!isSuccessfullyCleared) {
            world.playSound(player.getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f);
            player.sendMessage(ChatColor.YELLOW + "Game Over! Du konntest das Minecleaner-Feld nicht erfolgreich lösen!");
            arena.showMines();
            
            sg = statisticsTotalGamesPlayed.get(arena.getWidthIndex());

            if(sg != null) {
                ps.increaseScore(sg, 1);
            }

            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if(arena.getCurrentPlayer() == null) {
                    arena.removePlayer(); 
                 } else {
                    leaveArena(player, false);
                 }
            }, 100L);
            return;
        }
        int millis = (int) (System.currentTimeMillis() - arena.getCurrentGameStartTime());
        player.sendMessage(ChatColor.YELLOW + "Glückwunsch, du konntest das Minecleaner-Feld in " + ChatColor.RED + MinecleanerStringUtil.timeToString(millis) + ChatColor.YELLOW + " erfolgreich lösen!");
        
        world.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);

        ps.increaseScore(statisticsWonGamesTotal, 1);

        sg = statisticsGames.get(arena.getWidthIndex());
        if(sg != null) {
            ps.increaseScore(sg, 1);
        }
        sg = statisticsTimeRecord.get(arena.getWidthIndex());
        if(sg != null) {
            ps.minScore(sg, millis);
        }

        int wIndex = arena.getWidthIndex();
        switch (wIndex) {
            case 0: {
                ps.increaseScore(statisticsPointsAcquired, 1);
                break;
            }
            case 1: {
                ps.increaseScore(statisticsPointsAcquired, 3);
                break;
            }
            case 2: {
                ps.increaseScore(statisticsPointsAcquired, 5);
                break;
            }
            default: {
                ps.increaseScore(statisticsPointsAcquired, 0);
                break;
            }
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(arena.getCurrentPlayer() == null) {
               arena.removePlayer(); 
            } else {
                leaveArena(player, false);
            }
        }, 100L);
    }

    public void clearAllArenas() {
        for(MinecleanerArena arena : plugin.getArenaList().getArenas()) {
            if(arena.hasPlayer()) {
                leaveArena(arena.getCurrentPlayer(), true);
            }
        }
    }

    public Inventory getConfirmPlayingInventory() {
        return confirmPlayingInventory;
    }

    public void handleFieldClick(@NotNull Player player, int x, int y, boolean hasRightClicked) {
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
        Preconditions.checkArgument(arena != null, "player is in no arena");
        Preconditions.checkState(arena.getArenaStatus() == ArenaStatus.PLAYING, "not running");

        if(hasRightClicked) {
            arena.flagCell(x, y);
        } else {
            arena.revealCell(x, y);
        }     
    }

    public void getStatisticsForPlayer(OfflinePlayer player, Consumer<PlayerStatisticsData> callback) {
        List<StatisticsQueryKey> keys = new ArrayList<>();
        PlayerStatistics pStatistics = plugin.getCubesideStatistics().getStatistics(player.getUniqueId());

        PlayerStatisticsQueryKey kMatchesPlayed;
        keys.add(kMatchesPlayed = new PlayerStatisticsQueryKey(pStatistics, statisticsWonGamesTotal, QueryType.SCORE));
        PlayerStatisticsQueryKey kMatchesPlayedMonth;
        keys.add(kMatchesPlayedMonth = new PlayerStatisticsQueryKey(pStatistics, statisticsWonGamesTotal, QueryType.SCORE, TimeFrame.MONTH));

        HashMap<Integer, PlayerStatisticsQueryKey> kWonGamesPlayedSize = new HashMap<>();
        HashMap<Integer, PlayerStatisticsQueryKey> kWonGamesPlayedSizeMonth = new HashMap<>();
        HashMap<Integer, PlayerStatisticsQueryKey> kSizeTimeRecord = new HashMap<>();
        HashMap<Integer, PlayerStatisticsQueryKey> kSizeTotalGamesPlayed = new HashMap<>();
        HashMap<Integer, PlayerStatisticsQueryKey> kSizeTotalGamesPlayedMonth = new HashMap<>();

        for (int i = 0; i < BoardSize.boardSizes.length; i++) {
            PlayerStatisticsQueryKey qk;
            StatisticKey statisticKeyGames = statisticsGames.get(i);
            keys.add(qk = new PlayerStatisticsQueryKey(pStatistics, statisticKeyGames, QueryType.SCORE));
            kWonGamesPlayedSize.put(i, qk);
            keys.add(qk = new PlayerStatisticsQueryKey(pStatistics, statisticKeyGames, QueryType.SCORE, TimeFrame.MONTH));
            kWonGamesPlayedSizeMonth.put(i, qk);

            StatisticKey statisticKeyTotalGames = statisticsTotalGamesPlayed.get(i);
            keys.add(qk = new PlayerStatisticsQueryKey(pStatistics, statisticKeyTotalGames, QueryType.SCORE));
            kSizeTotalGamesPlayed.put(i, qk);
            keys.add(qk = new PlayerStatisticsQueryKey(pStatistics, statisticKeyTotalGames, QueryType.SCORE, TimeFrame.MONTH));
            kSizeTotalGamesPlayedMonth.put(i, qk);            

            StatisticKey statisticKeyTime = statisticsTimeRecord.get(i);
            keys.add(qk = new PlayerStatisticsQueryKey(pStatistics, statisticKeyTime, QueryType.SCORE));
            kSizeTimeRecord.put(i, qk);
        }

        PlayerStatisticsQueryKey kPointsAcquired;
        keys.add(kPointsAcquired = new PlayerStatisticsQueryKey(pStatistics, statisticsPointsAcquired, QueryType.SCORE));
        PlayerStatisticsQueryKey kPointsAcquiredMonth;
        keys.add(kPointsAcquiredMonth = new PlayerStatisticsQueryKey(pStatistics, statisticsPointsAcquired, QueryType.SCORE, TimeFrame.MONTH));

        plugin.getCubesideStatistics().queryStats(keys, (c) -> {
            int matchesPlayed = c.getOrDefault(kMatchesPlayed, 0);
            int matchesPlayedMonth = c.getOrDefault(kMatchesPlayedMonth, 0);
            int pointsAcquiredTotal = c.getOrDefault(kPointsAcquired, 0);
            int pointsAcquiredMonth = c.getOrDefault(kPointsAcquiredMonth, 0);

            HashMap<Integer, Integer> sizeWonGames = new HashMap<>();
            HashMap<Integer, Integer> sizeWonGamesMonth = new HashMap<>();
            HashMap<Integer, Integer> sizeTimeRecord = new HashMap<>();
            HashMap<Integer, Integer> sizeTotalGamesPlayed = new HashMap<>();
            HashMap<Integer, Integer> sizeTotalGamesPlayedMonth = new HashMap<>();

            for(int i = 0; i < BoardSize.boardSizes.length; i++) {
                sizeWonGames.put(i, c.getOrDefault(kWonGamesPlayedSize.get(i), 0));
                sizeWonGamesMonth.put(i, c.getOrDefault(kWonGamesPlayedSizeMonth.get(i), 0));
                sizeTimeRecord.put(i, c.getOrDefault(kSizeTimeRecord.get(i), null));
                sizeTotalGamesPlayed.put(i, c.getOrDefault(kSizeTotalGamesPlayed.get(i), 0));
                sizeTotalGamesPlayedMonth.put(i, c.getOrDefault(kSizeTotalGamesPlayedMonth.get(i), 0));
            }

            callback.accept(new PlayerStatisticsData(player.getUniqueId(), player.getName(), 
                sizeTotalGamesPlayed, 
                sizeTotalGamesPlayedMonth, 
                matchesPlayed, 
                matchesPlayedMonth,  
                sizeWonGames, 
                sizeWonGamesMonth, 
                pointsAcquiredTotal, 
                pointsAcquiredMonth,
                sizeTimeRecord));
        });
    }

    public void getStatisticsForPlayerIfExists(String player, Consumer<PlayerStatisticsData> callback) {
        CachedPlayer cPlayer = plugin.getPlayerUUIDCache().getPlayerFromNameOrUUID(player);
        if(cPlayer == null) {
            callback.accept(null);
        } else {
            getStatisticsForPlayer(cPlayer, callback);
        }
    }

    public void deleteScores(UUID playerId) {
        PlayerStatistics statsPlayer = plugin.getCubesideStatistics().getStatistics(playerId);
        statsPlayer.deleteScore(statisticsWonGamesTotal);
        statsPlayer.deleteScore(statisticsPointsAcquired);
    }
}
