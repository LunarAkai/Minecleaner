package de.lunarakai.minecleaner;

import de.iani.cubesidestats.api.SettingKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
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
    public BukkitTask schedulerGameOver;

    // Statistics
    private final StatisticKey statisticsWonGamesTotal;
    private final StatisticKey statisticsPointsAcquired;
    private final HashMap<Integer, StatisticKey> statisticsGames;
    private final HashMap<Integer, StatisticKey> statisticsTimeRecord;
    private final HashMap<Integer, StatisticKey> statisticsTotalGamesPlayed;

    // Settings

    private Inventory settingsInventory;
    private SettingKey minecleanerSettingTimerKey;
    private SettingKey minecleanerAdditionalDisplaySettingKey;
    private SettingKey minecleanerResetTimerSettingKey;
    private SettingKey minecleanerAllowManualResetSettingKey;

    public MinecleanerManager(MinecleanerPlugin plugin) {
        this.plugin = plugin;
        
        this.sizes = new HashMap<>();
        this.sizes.put(0, "klein");
        this.sizes.put(1, "mittel");
        this.sizes.put(2, "groß");
        this.sizes.put(3, "experte");

        this.confirmPlayingInventory = plugin.getServer().createInventory(null, InventoryType.HOPPER, plugin.getDisplayedPluginName() + " starten?");
        this.confirmPlayingInventory.setItem(1,
            ItemStacks.lore(ItemStacks.rename(new ItemStack(Material.GREEN_CONCRETE), ChatColor.GREEN + "Bestätigen")));
        this.confirmPlayingInventory.setItem(3,
            ItemStacks.lore(ItemStacks.rename(new ItemStack(Material.RED_CONCRETE), ChatColor.RED + "Abbrechen")));

        // Settings

        if(plugin.isStatisticsEnabled()) {
            minecleanerSettingTimerKey = plugin.getCubesideStatistics().getSettingKey("minecleaner.settings.timer");
            minecleanerSettingTimerKey.setDefault(0);
            minecleanerSettingTimerKey.setDisplayName("Timer");

            minecleanerAdditionalDisplaySettingKey = plugin.getCubesideStatistics().getSettingKey("minecleaner.settings.additionaldisplay");
            minecleanerAdditionalDisplaySettingKey.setDefault(0);
            minecleanerAdditionalDisplaySettingKey.setDisplayName("Zusätzliche Anzeige in der Action Bar");

            minecleanerResetTimerSettingKey = plugin.getCubesideStatistics().getSettingKey("minecleaner.settings.resettime");
            minecleanerResetTimerSettingKey.setDefault(5);
            minecleanerResetTimerSettingKey.setDisplayName("Dauer die das Spielfeld für das Zurücksetzen brauchen soll");

            minecleanerAllowManualResetSettingKey = plugin.getCubesideStatistics().getSettingKey("minecleaner.settings.allowmanualreset");
            minecleanerAllowManualResetSettingKey.setDefault(0);
            minecleanerAllowManualResetSettingKey.setDisplayName("Erlaube das manuelle Zurücksetzen des Spielfeldes");

            this.settingsInventory = plugin.getServer().createInventory(null, InventoryType.CHEST,
                    plugin.getDisplayedPluginName() + " Einstellungen");


            // Statistics

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
                s.setIsMonthlyStats(true);
                s.setDisplayName("Bestzeit bei Größe " + sizeDisplay);
                statisticsTimeRecord.put(e.getKey(), s);
                }
            } else {
            this.statisticsWonGamesTotal = null;
            this.statisticsPointsAcquired = null;
            this.statisticsGames = null;
            this.statisticsTimeRecord = null;
            this.statisticsTotalGamesPlayed = null;
        }
    }


    public void joinArena(Player[] players, MinecleanerArena arena) {
        if ((plugin.getGroupManager().getGroup(players[0]) == null && !players[0].hasPermission(MinecleanerPlugin.PERMISSION_PLAY)) || (plugin.getGroupManager().getGroup(players[0]) != null && !Bukkit.getPlayer(plugin.getGroupManager().getGroup(players[0]).getOwner()).hasPermission(MinecleanerPlugin.PERMISSION_PLAY))) {
            return;
        }

        Preconditions.checkArgument(plugin.getArenaList().getPlayerArena(players) == null, "player is in an arena");
        Preconditions.checkArgument(arena.getArenaStatus() == ArenaStatus.INACTIVE, "arena is in use");

        arena.addJoiningPlayers(players);
        plugin.getArenaList().setArenaForPlayers(players, arena);
        for(int i = 0; i < players.length; i++) {
            if(plugin.getGroupManager().getGroup(players[i]) == null) {
                players[i].openInventory(confirmPlayingInventory);
                break;
            }
            if(players[i] == Bukkit.getPlayer(plugin.getGroupManager().getGroup(players[i]).getOwner())) {
                players[i].openInventory(confirmPlayingInventory);
            }
        }
    }

    public void leaveArena(Player[] players, boolean message, boolean reset) {
        MinecleanerGroupManager.MinecleanerGroup group = null;
        MinecleanerArena arena;
        if(plugin.getGroupManager().getGroup(players[0]) != null) {
            group = plugin.getGroupManager().getGroup(players[0]);
            arena = plugin.getArenaList().getPlayerArena(Objects.requireNonNull(Bukkit.getPlayer(group.getOwner())));
        } else {
            arena = plugin.getArenaList().getPlayerArena(players);
        }
        Player[] players1 = group != null ? new Player[group.getPlayers().size()] : new Player[1];
        if(plugin.getGroupManager().getGroup(players[0]) != null) {
            int i = 0;
            for(Iterator<UUID> iterator = group.getPlayers().iterator(); iterator.hasNext();) {
                Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                players1[i] = iteratorPlayer;
                i++;
            }
        } else {
            players1 = players;
        }

        Preconditions.checkArgument(arena != null, "player is in no arena");

        if(reset) {
            arena.setArenaStaus(ArenaStatus.INACTIVE);
            for(int i = 0; i < players1.length; i++) {
                players1[i].closeInventory();
            }
            arena.removePlayers();
            if(message) {
                for(int i = 0; i < players.length; i++) {
                    players1[i].sendMessage(ChatColor.YELLOW + "Das " + plugin.getDisplayedPluginName() + "spiel wurde abgebrochen.");
                }
            }
        }

        plugin.getArenaList().setArenaForPlayers(players1, null);
    }


    public void startGame(Player[] players) {
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(players);
        Preconditions.checkArgument(arena != null, "player is in no arena");
        Preconditions.checkState(arena.getArenaStatus() == ArenaStatus.CONFIRM_PLAYING, "not confirming playing status");
        arena.startNewGame();


        if(plugin.getGroupManager().getGroup(players[0]) != null) {
            for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(players[0]).getPlayers().iterator(); iterator.hasNext();) {
                Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                assert iteratorPlayer != null;
                iteratorPlayer.sendMessage(Component.text("Du hast eine neue Runde " + plugin.getDisplayedPluginName() + " gestartet.", NamedTextColor.YELLOW));
            }
        } else {
            players[0].sendMessage(Component.text("Du hast eine neue Runde " + plugin.getDisplayedPluginName() + " gestartet.", NamedTextColor.YELLOW));
        }
    }

    public void handleGameover(Player[] player, MinecleanerArena arena, boolean isSuccessfullyCleared) {
        if(plugin.getGroupManager().getGroup(player[0]) != null) {
            World world = player[0].getWorld();
            if(!isSuccessfullyCleared) {
                world.playSound(player[0].getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f);

                int arraySize = plugin.getGroupManager().getGroup(player[0]) != null ? plugin.getGroupManager().getGroup(player[0]).getPlayers().size() : 1;
                Player[] players = new Player[arraySize];

                for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player[0]).getPlayers().iterator(); iterator.hasNext();) {
                        Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                    assert iteratorPlayer != null;
                    iteratorPlayer.sendMessage(Component.text("Game Over! Ihr konntest das " + plugin.getDisplayedPluginName() + "-Feld nicht erfolgreich lösen!", NamedTextColor.YELLOW));
                }

                arena.showMines();
                scheduleArenaReset(player[0], arena);
                return;
            }
            // Todo: Punkte durch Anzahl der Leute in der Gruppe teilen => bei floats abrunden (heißt für Kleine (1 Punkt normal) => 0 Punkte in der Gruppe)
            int millis = (int) (System.currentTimeMillis() - arena.getCurrentGameStartTime());
            world.playSound(player[0].getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);
            int arraySize = plugin.getGroupManager().getGroup(player[0]) != null ? plugin.getGroupManager().getGroup(player[0]).getPlayers().size() : 1;
            Player[] players = new Player[arraySize];

            for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player[0]).getPlayers().iterator(); iterator.hasNext();) {
                Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                assert iteratorPlayer != null;
                iteratorPlayer.sendMessage(Component.text(
                        "Glückwunsch, ihr konntest das " + plugin.getDisplayedPluginName() + "-Feld in ", NamedTextColor.YELLOW)
                        .append(Component.text(MinecleanerStringUtil.timeToString(millis, false), NamedTextColor.RED))
                        .append(Component.text(" erfolgreich lösen!", NamedTextColor.YELLOW)));
            }
            scheduleArenaReset(player[0], arena);
            return;
        }
        World world = player[0].getWorld();
        PlayerStatistics ps = null;
        StatisticKey sg = null;
        if(plugin.isStatisticsEnabled()) {
            ps = plugin.getCubesideStatistics().getStatistics(player[0].getUniqueId());
            sg = statisticsTotalGamesPlayed.get(arena.getWidthIndex());
        }

        if(!isSuccessfullyCleared) {
            world.playSound(player[0].getLocation(), Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 0.5f);
            player[0].sendMessage(ChatColor.YELLOW + "Game Over! Du konntest das " + plugin.getDisplayedPluginName() + "-Feld nicht erfolgreich lösen!");
            arena.showMines();
            
            if(sg != null && plugin.isStatisticsEnabled()) {
                ps.increaseScore(sg, 1);
            }

            scheduleArenaReset(player[0], arena);
            return;
        }
        int millis = (int) (System.currentTimeMillis() - arena.getCurrentGameStartTime());
        
        world.playSound(player[0].getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.5f, 0.5f);

        if(sg != null && plugin.isStatisticsEnabled()) {
            ps.increaseScore(sg, 1);
        }

        if(plugin.isStatisticsEnabled()) {
            ps.increaseScore(statisticsWonGamesTotal, 1);

            sg = statisticsGames.get(arena.getWidthIndex());
            if(sg != null) {
                ps.increaseScore(sg, 1);
            }
            sg = statisticsTimeRecord.get(arena.getWidthIndex());
            if(sg != null) {
                ps.minScore(sg, millis, isUpdated -> {
                    if(isUpdated != null && isUpdated) {
                        player[0].sendMessage(ChatColor.GOLD + "Herzlichen Glückwunsch! Du hast eine neue Bestzeit erreicht! " + ChatColor.RED + MinecleanerStringUtil.timeToString(millis, false) );
                    } else {
                        player[0].sendMessage(ChatColor.YELLOW + "Glückwunsch, du konntest das " + plugin.getDisplayedPluginName() + "-Feld in " + ChatColor.RED + MinecleanerStringUtil.timeToString(millis, false) + ChatColor.YELLOW + " erfolgreich lösen!");
                    }
                });
            }

            int wIndex = arena.getWidthIndex();
            switch (wIndex) {
                case 0: {
                    ps.increaseScore(statisticsPointsAcquired, plugin.getConfig().getInt("winpoints.size.small"));
                    break;
                }
                case 1: {
                    ps.increaseScore(statisticsPointsAcquired, plugin.getConfig().getInt("winpoints.size.medium"));
                    break;
                }
                case 2: {
                    ps.increaseScore(statisticsPointsAcquired, plugin.getConfig().getInt("winpoints.size.large"));
                    break;
                }
                case 3: {
                    ps.increaseScore(statisticsPointsAcquired, plugin.getConfig().getInt("winpoints.size.expert"));
                }
                default: {
                    ps.increaseScore(statisticsPointsAcquired, 0);
                    break;
                }
            }
        } else {
            player[0].sendMessage(ChatColor.YELLOW + "Glückwunsch, du konntest das " + plugin.getDisplayedPluginName() + "-Feld in " + ChatColor.RED + MinecleanerStringUtil.timeToString(millis, false) + ChatColor.YELLOW + " erfolgreich lösen!");
        }

        scheduleArenaReset(player[0], arena);
    }

    private void scheduleArenaReset(Player player, MinecleanerArena arena) {
        schedulerGameOver = Bukkit.getScheduler().runTaskLater(plugin, () -> {
            if(arena.getArenaStatus() == ArenaStatus.COMPLETED) {
                if (arena.getCurrentPlayers() == null) {
                    arena.removePlayers();
                } else {
                    int arraySize = plugin.getGroupManager().getGroup(player) != null ? plugin.getGroupManager().getGroup(player).getPlayers().size() : 1;
                    Player[] players = new Player[arraySize];
                    int i = 0;
                    if(plugin.getGroupManager().getGroup(player) != null) {
                        for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
                            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                            players[i] = iteratorPlayer;
                            i++;
                        }
                    } else {
                        Arrays.fill(players, player);
                    }
                    leaveArena(players, false, true);
                }
            }
        }, plugin.getManager().getSettingsValue("resettime", player) * 20L);
    }

    public void clearAllArenas() {
        for(MinecleanerArena arena : plugin.getArenaList().getArenas()) {
            if(arena.hasPlayers()) {
                leaveArena(arena.getCurrentPlayers(), true, true);
            }
        }
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
        HashMap<Integer, PlayerStatisticsQueryKey> kSizeTimeRecordMonth = new HashMap<>();
        HashMap<Integer, PlayerStatisticsQueryKey> kSizeTotalGamesPlayed = new HashMap<>();
        HashMap<Integer, PlayerStatisticsQueryKey> kSizeTotalGamesPlayedMonth = new HashMap<>();

        for (int i = 0; i < BoardSize.boardSizesWidth.length; i++) {
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
            keys.add(qk = new PlayerStatisticsQueryKey(pStatistics, statisticKeyTime, QueryType.SCORE, TimeFrame.MONTH));
            kSizeTimeRecordMonth.put(i, qk);
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
            HashMap<Integer, Integer> sizeTimeRecordMonth = new HashMap<>();
            HashMap<Integer, Integer> sizeTotalGamesPlayed = new HashMap<>();
            HashMap<Integer, Integer> sizeTotalGamesPlayedMonth = new HashMap<>();

            for(int i = 0; i < BoardSize.boardSizesWidth.length; i++) {
                sizeWonGames.put(i, c.getOrDefault(kWonGamesPlayedSize.get(i), 0));
                sizeWonGamesMonth.put(i, c.getOrDefault(kWonGamesPlayedSizeMonth.get(i), 0));
                sizeTimeRecord.put(i, c.getOrDefault(kSizeTimeRecord.get(i), null));
                sizeTimeRecordMonth.put(i, c.getOrDefault(kSizeTimeRecordMonth.get(i), null));
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
                sizeTimeRecord,
                sizeTimeRecordMonth));
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
        for(StatisticKey statsKey : statisticsGames.values()) {
            statsPlayer.deleteScore(statsKey);
        }
        for(StatisticKey statsKey : statisticsTimeRecord.values()) {
            statsPlayer.deleteScore(statsKey);
        }
        for(StatisticKey statsKey : statisticsTotalGamesPlayed.values()) {
            statsPlayer.deleteScore(statsKey);
        }
    }

    public int getSettingsValue(String settingsKeyString, Player player) {
        PlayerStatistics playerStatistics = plugin.getCubesideStatistics().getStatistics(player.getUniqueId());
        SettingKey settingKey = plugin.getCubesideStatistics().getSettingKey("minecleaner.settings." + settingsKeyString);

        return playerStatistics.getSettingValueOrDefault(settingKey);
    }

    public void updateSettingsValue(String settingsKeyString, int newValue, Player player) {
        PlayerStatistics playerStatistics = plugin.getCubesideStatistics().getStatistics(player.getUniqueId());
        SettingKey settingKey = plugin.getCubesideStatistics().getSettingKey("minecleaner.settings." + settingsKeyString);
        playerStatistics.setSettingValue(settingKey, newValue);
    }

    public HashMap<Integer, String> getSizes() {
        return sizes;
    }

    public Inventory getConfirmPlayingInventory() {
        return confirmPlayingInventory;
    }

    public Inventory getSettingsInventory() {
        return settingsInventory;
    }

    public SettingKey getMinecleanerSettingTimerKey() {
        return minecleanerSettingTimerKey;
    }

    public SettingKey getMinecleanerAdditionalDisplaySettingKey() {
        return minecleanerAdditionalDisplaySettingKey;
    }

    public SettingKey getMinecleanerResetTimeSettingKey() {return minecleanerResetTimerSettingKey; }

    public BukkitTask getSchedulerGameOver() { return schedulerGameOver; }
}
