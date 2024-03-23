package de.lunarakai.minecleaner.commands;

import java.util.Map.Entry;
import java.util.function.Consumer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.iani.cubesideutils.StringUtil;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import de.lunarakai.minecleaner.PlayerStatisticsData;

public class StatsCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public StatsCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "[name]";
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public String getRequiredPermission() {
        return MinecleanerPlugin.PERMISSION_PLAY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        String playerName = args.getNext(null);

        Consumer<PlayerStatisticsData> callback = new Consumer<>() {
            @Override
            public void accept(PlayerStatisticsData data) {
                if(data == null) {
                    sender.sendMessage(ChatColor.GREEN + "Für Spieler '" + playerName + "' existieren keine Daten.");
                    return;
                }
                if(playerName == null) {
                    sender.sendMessage(ChatColor.GREEN + "Deine Minecleaner Statistik:");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "Minecleaner-Statitik von " + data.getPlayerName() + ":");
                }
                sender.sendMessage(ChatColor.BLUE + "  Punkte erspielt: " + ChatColor.GREEN + data.getPointsAcquiredTotal() + " (Dieser Monat: " + data.getPointsAquiredMonth() + ")");
                sender.sendMessage(ChatColor.BLUE + "  Runden gespielt: " + ChatColor.GREEN + data.getGamesPlayed() + " (Dieser Monat: " + data.getGamesPlayedThisMonth() + ")");
                for(Entry<Integer, String> e : plugin.getManager().getSizes().entrySet()) {
                    int totalSize = data.getGamesPlayedSize(e.getKey());
                    if(totalSize > 0) {
                        String sizeName = StringUtil.capitalizeFirstLetter(e.getValue(), false);
                        sender.sendMessage(ChatColor.GREEN + "  " + sizeName + ":");
                        sender.sendMessage(ChatColor.BLUE + "    Runden gespielt: " + ChatColor.GREEN + totalSize + " (Dieser Monat: " + data.getGamesPlayedSizeThisMonth(e.getKey()) + ")");
                    }
                }

            }
        };
        if(playerName == null) {
            if(sender instanceof Player) {
                plugin.getManager().getStatisticsForPlayer((Player) sender, callback);
            } else {
                sender.sendMessage(ChatColor.GREEN + "Für die Konsole existieren keine Daten.");
            }
        } else {
            plugin.getManager().getStatisticsForPlayerIfExists(playerName, callback);
        }
        return true;
    }
    
}
