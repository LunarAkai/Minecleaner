package de.lunarakai.minecleaner.commands;

import java.util.Map.Entry;
import java.util.function.Consumer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import de.lunarakai.minecleaner.PlayerStatisticsData;
import de.lunarakai.minecleaner.utils.MinecleanerStringUtil;

import static de.lunarakai.minecleaner.utils.MinecleanerComponentUtils.createLangComponent;

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
                    sender.sendMessage(createLangComponent("data.player.noData", playerName, NamedTextColor.GREEN));
                    return;
                }
                if(playerName == null) {
                    sender.sendMessage(createLangComponent("data.player.self", plugin.getDisplayedPluginName(), NamedTextColor.AQUA).append(Component.text(":")));
                } else {
                    sender.sendMessage(createLangComponent("data.player.other", plugin.getDisplayedPluginName(), data.getPlayerName(), NamedTextColor.AQUA).append(Component.text(":")));
                }

                sender.sendMessage(createLangComponent("data.player.pointsscored", NamedTextColor.BLUE)
                        .append(Component.text(": ", NamedTextColor.BLUE))
                        .append(Component.text(String.valueOf(data.getPointsAcquiredTotal()), NamedTextColor.GREEN))
                        .append(Component.text(" (", NamedTextColor.GREEN))
                        .append(createLangComponent("data.player.thismonth", NamedTextColor.GREEN))
                        .append(Component.text(": " + String.valueOf(data.getPointsAquiredMonth()) + ")", NamedTextColor.GREEN)));


                sender.sendMessage(createLangComponent("data.player.roundswon", NamedTextColor.BLUE)
                        .append(Component.text(": ", NamedTextColor.BLUE))
                        .append(Component.text(String.valueOf(data.getWonGamesPlayed()), NamedTextColor.GREEN))
                        .append(Component.text(" (", NamedTextColor.GREEN))
                        .append(createLangComponent("data.player.thismonth", NamedTextColor.GREEN))
                        .append(Component.text(": " + String.valueOf(data.getWonGamesPlayedThisMonth()) + ")", NamedTextColor.GREEN)));

                for(Entry<Integer, String> e : plugin.getManager().getSizes().entrySet()) {
                    int totalWonSize = data.getGamesPlayedSize(e.getKey());
                    int totalWonMonth = data.getGamesPlayedSizeThisMonth(e.getKey());
                    int totalSize = data.getTotalGamesPlayedSize(e.getKey());
                    int totalSizeMonth = data.getTotalGamesPlayedSizeThisMonth(e.getKey());

                    if(totalSize > 0) {
                        String sizeName = e.getValue();
                        if(sizeName.equals("groß")) {
                            sizeName = "gross";
                        }
                        sender.sendMessage(createLangComponent("arena.width." + sizeName, NamedTextColor.AQUA).append(Component.text(":", NamedTextColor.AQUA)));

                        sender.sendMessage(Component.text("    ")
                                                .append(createLangComponent("data.player.roundswon", NamedTextColor.BLUE))
                                                .append(Component.text(" "))
                                                .append(Component.text(String.valueOf(totalWonSize), NamedTextColor.GREEN))
                                                .append(Component.text(" "))
                                                .append(createLangComponent("data.player.outof", NamedTextColor.GREEN))
                                                .append(Component.text(" " + totalSize + " (" + MinecleanerStringUtil.percentageString(totalWonSize, totalSize) + ")", NamedTextColor.GREEN)));

                        sender.sendMessage(Component.text("    ")
                                .append(createLangComponent("data.player.thismonth", NamedTextColor.BLUE))
                                .append(Component.text(" "))
                                .append(Component.text(String.valueOf(totalWonMonth), NamedTextColor.GREEN))
                                .append(Component.text(" "))
                                .append(createLangComponent("data.player.outof", NamedTextColor.GREEN))
                                .append(Component.text(" " + totalSize + " (" + MinecleanerStringUtil.percentageString(totalWonMonth, totalSizeMonth) + ")", NamedTextColor.GREEN)));

                        Integer time = data.getBestTime(e.getKey());
                        Integer timeThisMonth = data.getBestTimeThisMonth(e.getKey());
                        sender.sendMessage(Component.text("    ")
                                .append(createLangComponent("data.player.besttime",": ", (time == null ? "-" : MinecleanerStringUtil.timeToString(time, false)), NamedTextColor.BLUE, NamedTextColor.GREEN)));

                        sender.sendMessage(Component.text("    ")
                                .append(createLangComponent("data.player.thismonth", ": ", (timeThisMonth == null ? "-" : MinecleanerStringUtil.timeToString(timeThisMonth, false)), NamedTextColor.BLUE, NamedTextColor.GREEN)));
                    }
                }
            }
        };
        if(playerName == null) {
            if(sender instanceof Player) {
                plugin.getManager().getStatisticsForPlayer((Player) sender, callback);
            } else {
                sender.sendMessage(createLangComponent("data.console.nodata", NamedTextColor.GREEN));
            }
        } else {
            plugin.getManager().getStatisticsForPlayerIfExists(playerName, callback);
        }
        return true;
    }
    
}
