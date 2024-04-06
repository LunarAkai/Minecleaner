package de.lunarakai.minecleaner.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.iani.playerUUIDCache.CachedPlayer;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.md_5.bungee.api.ChatColor;

public class DeletePlayerScoreCommand extends SubCommand{
    private final MinecleanerPlugin plugin;

    public DeletePlayerScoreCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<name> DELETE";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return MinecleanerPlugin.PERMISSION_ADMIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String commandString,
            ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException,
            NoPermissionException, IllegalSyntaxException, InternalCommandException {
        String player = args.getNext(null);
        String deleteConfirm = args.getNext(null);
        if(player == null || deleteConfirm == null || !deleteConfirm.equals("DELETE") || args.remaining() > 0) {
            sender.sendMessage(ChatColor.DARK_RED + commandString + getUsage());
            return true;
        }

        CachedPlayer cachedPlayer = plugin.getPlayerUUIDCache().getPlayer(player);
        if(cachedPlayer == null) {
            sender.sendMessage(ChatColor.DARK_RED + "Ein Spieler mit dem Namen '" + player + "' konnte nicht gefunden werden.");
        }
        plugin.getManager().deleteScores(cachedPlayer.getUUID());
        sender.sendMessage(ChatColor.DARK_RED + "Alle " + plugin.getDisplayedPluginName() + "-Statistiken von Spieler '" + cachedPlayer.getName() + "' wurden gelöscht.");
        return true;
    }
    
}
