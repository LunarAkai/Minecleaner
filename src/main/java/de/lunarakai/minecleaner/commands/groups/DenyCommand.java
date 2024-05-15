package de.lunarakai.minecleaner.commands.groups;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.*;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            plugin.getGroupManager().getInvitedGroup(player).removePlayerFromInvitedList(player);

            assert groupOwner != null;
            groupOwner.sendMessage(Component.text(player.getName() + " hat deine Einladung abgelehnt.", NamedTextColor.RED));
            player.sendMessage(Component.text("Du hast die Einladung abgelehnt", NamedTextColor.YELLOW));

            return true;
        } else {
            player.sendMessage(Component.text("Du wurdest in keine Gruppe eingeladen.", NamedTextColor.YELLOW));
        }
        return true;
    }
}
