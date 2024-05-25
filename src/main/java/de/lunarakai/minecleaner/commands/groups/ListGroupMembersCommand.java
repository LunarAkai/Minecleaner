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
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ListGroupMembersCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public ListGroupMembersCommand(MinecleanerPlugin plugin) {
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
            ChatUtils.sendSimpleInfoMessage(player, "group.common.notingroup");
            return true;
        }

        ChatUtils.sendSimpleSpecialMessage(player, "-- Mitglieder deiner " + plugin.getDisplayedPluginName() + "gruppe --", NamedTextColor.AQUA);
        for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
            String ownerString = "";
            if(iteratorPlayer == Bukkit.getPlayer(plugin.getGroupManager().getGroup(player).getOwner())) {
                ownerString = " (Ersteller der Gruppe)";
            }
            player.sendMessage(Component.text("  - " + iteratorPlayer.getName(), NamedTextColor.GREEN).append(Component.text(ownerString, NamedTextColor.RED)));
        }
        return true;
    }
}
