package de.lunarakai.minecleaner;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
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
import net.md_5.bungee.api.ChatColor;

public class MinecleanerManager {
    private final MinecleanerPlugin plugin;
    private final Inventory confirmPlayingInventory;
    private final StatisticKey statisticsGamesTotal;

    private int prevTick = 0;

    public MinecleanerManager(MinecleanerPlugin plugin) {
        this.plugin = plugin;

        // Deprecated
        this.confirmPlayingInventory = plugin.getServer().createInventory(null, InventoryType.HOPPER, "Minecleaner starten?");
        this.confirmPlayingInventory.setItem(1, 
            ItemStacks.lore(ItemStacks.rename(new ItemStack(Material.GREEN_CONCRETE), ChatColor.GREEN + "Bestätigen")));
        this.confirmPlayingInventory.setItem(3,
            ItemStacks.lore(ItemStacks.rename(new ItemStack(Material.RED_CONCRETE), ChatColor.RED + "Abbrechen")));


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
        if(!isSuccessfullyCleared) {
            player.sendMessage(ChatColor.YELLOW + "Game Over! Du konntest das Feld nicht erfolgreich lösen!");
            arena.showMines();
            
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                leaveArena(player, false);
            }, 100L);
            return;
        }
        player.sendMessage(ChatColor.YELLOW + "Glückwunsch, du konntest das Feld erfolgreich lösen!");
        PlayerStatistics ps = plugin.getCubesideStatistics().getStatistics(player.getUniqueId());
        ps.increaseScore(statisticsGamesTotal, 1);



        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            leaveArena(player, false);
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

        // Feels like a stupid solution for issue described in the next comment
        int currentTick = plugin.getServer().getCurrentTick();
        if(prevTick != currentTick) {
            // Fires Twice for Right Click on Same Tick, but only once for left click... stupid :< 
            if(hasRightClicked) {
                // flag
                plugin.getLogger().log(Level.SEVERE, "  Right Clicked @ Tick: " + plugin.getServer().getCurrentTick());
                arena.flagCell(x, y);
            } else {
                // reveal
                plugin.getLogger().log(Level.SEVERE, "  Left Clicked @ Tick: " + plugin.getServer().getCurrentTick());
                arena.revealCell(x, y);
            }
        }
        prevTick = currentTick;       
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
