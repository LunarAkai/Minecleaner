package de.lunarakai.minecleaner.commands.groups;

import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerGroupManager;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import de.lunarakai.minecleaner.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DenyCommand extends SubCommand {

    private final MinecleanerPlugin plugin;

    public DenyCommand(MinecleanerPlugin plugin) {
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

        if(plugin.getGroupManager().getInvitedGroup(player) != null && plugin.getGroupManager().getGroup(player) == null) {
            Player groupOwner = Bukkit.getPlayer(plugin.getGroupManager().getInvitedGroup(player).getOwner());
            MinecleanerGroupManager.MinecleanerGroup invitedGroup = plugin.getGroupManager().getInvitedGroup(player);
            invitedGroup.removePlayerFromInvitedList(player);
            if(plugin.getGroupManager().getGroup(Bukkit.getPlayer(invitedGroup.getOwner())).invitedPlayers.isEmpty()) {
                plugin.getGroupManager().deleteGroup(invitedGroup);
            }

            assert groupOwner != null;
            ChatUtils.sendSimpleWarningMessage(groupOwner, player.getName() + " hat deine Einladung abgelehnt.");
            ChatUtils.sendSimpleInfoMessage(player, "group.invite.denied");
            return true;
        } else {
            ChatUtils.sendSimpleWarningMessage(player, "group.invite.notinvited");
        }
        return true;
    }
}
