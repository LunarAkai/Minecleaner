package de.lunarakai.minecleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import com.google.common.base.Preconditions;
import de.iani.cubesidestats.api.PlayerStatistics;
import de.iani.cubesidestats.api.PlayerStatisticsQueryKey;
import de.iani.cubesidestats.api.PlayerStatisticsQueryKey.QueryType;
import de.iani.cubesidestats.api.StatisticKey;
import de.iani.cubesidestats.api.StatisticsQueryKey;
import de.iani.cubesidestats.api.TimeFrame;
import de.iani.playerUUIDCache.CachedPlayer;
import net.md_5.bungee.api.ChatColor;

public class MinecleanerManager {
    private final MinecleanerPlugin plugin;

    private final StatisticKey statisticsGamesTotal;

    public MinecleanerManager(MinecleanerPlugin plugin) {
        this.plugin = plugin;

        statisticsGamesTotal = plugin.getCubesideStatistics().getStatisticKey("minecleaner.gamesTotal");
        statisticsGamesTotal.setIsMonthlyStats(true);
        statisticsGamesTotal.setDisplayName("Runden gespielt");

    }
    
    public void joinArena(Player player, MinecleanerArena arena) {
        if (!player.hasPermission(MinecleanerPlugin.PERMISSION_PLAY)) {
            return;
        }
        Preconditions.checkArgument(plugin.getArenaList().getPlayerArena(player) == null, "player is in an arena");
        Preconditions.checkArgument(arena.getArenaStatus() == ArenaStatus.INACTIVE, "arena is in use");
        arena.addJoiningPlayer(player);
        plugin.getArenaList().setArenaForPlayer(player, arena);
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

    public void getStatisticsForPlayer(OfflinePlayer player, Consumer<PlayerStatisticsData> callback) {
        List<StatisticsQueryKey> keys = new ArrayList<>();
        PlayerStatistics pStatistics = plugin.getCubesideStatistics().getStatistics(player.getUniqueId());

        PlayerStatisticsQueryKey kMatchesPlayed;
        keys.add(kMatchesPlayed = new PlayerStatisticsQueryKey(pStatistics, statisticsGamesTotal, QueryType.SCORE));
        PlayerStatisticsQueryKey kMatchesPlayedMonth;
        keys.add(kMatchesPlayedMonth = new PlayerStatisticsQueryKey(pStatistics, statisticsGamesTotal, QueryType.SCORE, TimeFrame.MONTH));

        plugin.getCubesideStatistics().queryStats(keys, (c) -> {
            int matchesPlayed = c.getOrDefault(kMatchesPlayed, 0);
            int matchesPlayedMonth = c.getOrDefault(kMatchesPlayedMonth, 0);

            callback.accept(new PlayerStatisticsData(player.getUniqueId(), player.getName(), matchesPlayed, matchesPlayedMonth));
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
        statsPlayer.deleteScore(statisticsGamesTotal);
    }
}
