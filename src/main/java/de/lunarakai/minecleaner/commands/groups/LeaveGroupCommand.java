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

public class LeaveGroupCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public LeaveGroupCommand(MinecleanerPlugin plugin) {
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
    public boolean onCommand(CommandSender sender, Command command, String s, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        if(plugin.getGroupManager().getGroup(player) == null) {
            ChatUtils.sendSimpleWarningMessage(player, "group.common.notingroup");
            return true;
        }

        Player groupOwnerPlayer = Bukkit.getPlayer(plugin.getGroupManager().getGroup(player).getOwner());

        if(player == groupOwnerPlayer) {
            ChatUtils.sendSimpleWarningMessage(player, "group.leave.usedismantle");
            return true;
        }

        plugin.getGroupManager().getGroup(player).removePlayerFromGroup(player);
        return true;
    }

}
