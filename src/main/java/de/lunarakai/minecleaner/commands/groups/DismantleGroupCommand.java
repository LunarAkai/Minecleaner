package de.lunarakai.minecleaner.commands.groups;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import java.util.Iterator;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
            player.sendMessage(Component.text("Du bist in keiner Gruppe die du auflösen könntest.", NamedTextColor.YELLOW));
            return true;
        }
        Player groupOwnerPlayer = Bukkit.getPlayer(plugin.getGroupManager().getGroup(player).getOwner());

        if(player != groupOwnerPlayer) {
            player.sendMessage(Component.text("Du bist nicht berechtigt deine Gruppe aufzulösen.", NamedTextColor.YELLOW));
            return true;
        }


        for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
            iteratorPlayer.sendMessage(Component.text("Die Gruppe in der du dich befindest wurde aufgelöst.", NamedTextColor.YELLOW));
        }
        plugin.getGroupManager().deleteGroup(plugin.getGroupManager().getGroup(player));

        return true;
    }
}
