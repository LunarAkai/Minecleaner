package de.lunarakai.minecleaner.commands.groups;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import de.lunarakai.minecleaner.utils.ChatUtils;
import java.util.Iterator;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DismantleGroupCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public DismantleGroupCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean requiresPlayer() {
        return true;
    }

    @Override
    public String getRequiredPermission() {
        return MinecleanerPlugin.PERMISSION_PLAY;
    }


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String s1, ArgsParser argsParser) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) commandSender;
        if(plugin.getGroupManager().getGroup(player) == null) {
            ChatUtils.sendSimpleWarningMessage(player, "group.common.notingroup");
            return true;
        }
        Player groupOwnerPlayer = Bukkit.getPlayer(plugin.getGroupManager().getGroup(player).getOwner());

        if(player != groupOwnerPlayer) {
            ChatUtils.sendSimpleWarningMessage(player, "group.dismantle.nopermission");
            return true;
        }

        for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
            ChatUtils.sendSimpleWarningMessage(iteratorPlayer, "group.dismantle.yourgroup");
        }
        plugin.getGroupManager().deleteGroup(plugin.getGroupManager().getGroup(player));

        return true;
    }
}
