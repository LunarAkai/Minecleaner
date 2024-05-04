package de.lunarakai.minecleaner;

import de.lunarakai.minecleaner.commands.SettingsCommand;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.translation.GlobalTranslator;
import net.kyori.adventure.translation.TranslationRegistry;
import net.kyori.adventure.util.UTF8ResourceBundleControl;
import org.bukkit.plugin.java.JavaPlugin;
import de.iani.cubesidestats.api.CubesideStatisticsAPI;
import de.iani.cubesideutils.bukkit.commands.CommandRouter;
import de.iani.playerUUIDCache.PlayerUUIDCache;
import de.lunarakai.minecleaner.commands.CreateCommand;
import de.lunarakai.minecleaner.commands.DeleteCommand;
import de.lunarakai.minecleaner.commands.DeletePlayerScoreCommand;
import de.lunarakai.minecleaner.commands.InfoCommand;
import de.lunarakai.minecleaner.commands.ListCommand;
import de.lunarakai.minecleaner.commands.StatsCommand;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.logging.Level;

public final class MinecleanerPlugin extends JavaPlugin {
    public static final String PERMISSION_PLAY = "minecleaner.play";
    public static final String PERMISSION_ADMIN = "minecleaner.admin";

    private MinecleanerManager minecleanerManager;
    private ArenaList arenaList;
    private CubesideStatisticsAPI cubesideStatistics;
    private PlayerUUIDCache playerUUIDCache;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();

        TranslationRegistry registry = TranslationRegistry.create(Key.key("minecleaner:lang"));

        ResourceBundle bundle_en_US = ResourceBundle.getBundle("lang.en_US", Locale.US, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.US, bundle_en_US, true);
        GlobalTranslator.translator().addSource(registry);

        ResourceBundle bundle_de_DE = ResourceBundle.getBundle("lang.de_DE", Locale.GERMAN, UTF8ResourceBundleControl.get());
        registry.registerAll(Locale.GERMAN, bundle_de_DE, true);

        GlobalTranslator.translator().addSource(registry);

        getServer().getScheduler().runTask(this, this::onLateEnable);
    }

    public void onLateEnable() {
        if(getServer().getPluginManager().getPlugin("PlayerUUIDCache") != null) {
            playerUUIDCache = (PlayerUUIDCache) getServer().getPluginManager().getPlugin("PlayerUUIDCache");
        } else {
            this.getLogger().log(Level.WARNING, "PlayerUUIDCache not found.");
        }

        if(getServer().getPluginManager().getPlugin("CubesideStatistics") != null) {
            cubesideStatistics = getServer().getServicesManager().load(CubesideStatisticsAPI.class);
        } else {
            this.getLogger().log(Level.WARNING, "Cubeside Statistics not found. No Statistics will be available");
        }

        arenaList = new ArenaList(this);
        arenaList.load();

        minecleanerManager = new MinecleanerManager(this);
        getServer().getPluginManager().registerEvents(new MinecleanerListener(this), this);

        CommandRouter minecleanerCommand = new CommandRouter(getCommand("minecleaner"));
        minecleanerCommand.addCommandMapping(new CreateCommand(this), "create");
        minecleanerCommand.addCommandMapping(new DeleteCommand(this), "delete");
        minecleanerCommand.addCommandMapping(new ListCommand(this), "list");
        minecleanerCommand.addCommandMapping(new InfoCommand(this), "info");

        if(isStatisticsEnabled()) {
            minecleanerCommand.addCommandMapping(new SettingsCommand(this), "settings");
            minecleanerCommand.addCommandMapping(new StatsCommand(this), "stats");
            minecleanerCommand.addCommandMapping(new DeletePlayerScoreCommand(this), "deleteplayerscores");
        }
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

    public boolean isStatisticsEnabled() {
        return cubesideStatistics != null;
    }
    public CubesideStatisticsAPI getCubesideStatistics() {
        return cubesideStatistics;
    }

    public PlayerUUIDCache getPlayerUUIDCache() {
        return playerUUIDCache;
    }

    public String getDisplayedPluginName() {
        return this.getConfig().getString("generalSettings.displayedPluginName");
    }

    public int getSizeWinpoints(String size) {
        return this.getConfig().getInt("winpoints.size." + size);
    }
}
