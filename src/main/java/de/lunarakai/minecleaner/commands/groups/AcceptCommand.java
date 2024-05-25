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
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AcceptCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public AcceptCommand(MinecleanerPlugin plugin) {
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
            if(plugin.getArenaList().getPlayerArena(groupOwner) != null) {
                ChatUtils.sendSimpleWarningMessage(player, "Du kannst keine Einladung annehmen während die Person, die dich eingeladen hat, in einer Runde ist.");
                return true;
            }
            plugin.getGroupManager().getInvitedGroup(player).addPlayerToGroup(player);

            assert groupOwner != null;
            ChatUtils.sendSimpleSuccessMessage(groupOwner, player.getName() + " hat deine Einladung angenommen.");
            ChatUtils.sendSimpleSuccessMessage(player, "Du hast die Einladung angenommen");
            return true;
        } else {
            ChatUtils.sendSimpleInfoMessage(player, "Du wurdest in keine Gruppe eingeladen.");
        }
        return true;
    }
}
