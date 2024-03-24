package de.lunarakai.minecleaner;

import org.bukkit.plugin.java.JavaPlugin;
import de.iani.cubesidestats.api.CubesideStatisticsAPI;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.playerUUIDCache.PlayerUUIDCache;
import de.lunarakai.minecleaner.commands.CreateCommand;
import de.lunarakai.minecleaner.commands.DeleteCommand;
import de.lunarakai.minecleaner.commands.DeletePlayerScoreCommand;
import de.lunarakai.minecleaner.commands.ListCommand;
import de.lunarakai.minecleaner.commands.StatsCommand;

public final class MinecleanerPlugin extends JavaPlugin {
    public static final String PERMISSION_PLAY = "minecleaner.play";
    public static final String PERMISSION_ADMIN = "minecleaner.admin";

    private MinecleanerManager minecleanerManager;
    private ArenaList arenaList;
    private CubesideStatisticsAPI cubesideStatistics;
    private PlayerUUIDCache playerUUIDCache;

    @Override
    public void onEnable() {
        getServer().getScheduler().runTask(this, this::onLateEnable);
    }

    public void onLateEnable() {
        playerUUIDCache = (PlayerUUIDCache) getServer().getPluginManager().getPlugin("PlayerUUIDCache");
        cubesideStatistics = getServer().getServicesManager().load(CubesideStatisticsAPI.class);

        arenaList = new ArenaList(this);
        arenaList.load();

        minecleanerManager = new MinecleanerManager(this);
        getServer().getPluginManager().registerEvents(new MinecleanerListener(this), this);

        CommandRouter minecleanerCommand = new CommandRouter(getCommand("minecleaner"));
        minecleanerCommand.addCommandMapping(new CreateCommand(this), "create");
        minecleanerCommand.addCommandMapping(new DeleteCommand(this), "delete");
        minecleanerCommand.addCommandMapping(new ListCommand(this), "list");
        minecleanerCommand.addCommandMapping(new StatsCommand(this), "stats");
        minecleanerCommand.addCommandMapping(new DeletePlayerScoreCommand(this), "deleteplayerscores");
    }

    @Override
    public void onDisable() {
        if(minecleanerManager != null) {
            minecleanerManager.clearAllArenas();
        }
    }

    public ArenaList getArenaList() {
        return arenaList;
    }

    public MinecleanerManager getManager() {
        return minecleanerManager;
    }

    public CubesideStatisticsAPI getCubesideStatistics() {
        return cubesideStatistics;
    }

    public PlayerUUIDCache getPlayerUUIDCache() {
        return playerUUIDCache;
    }
}
