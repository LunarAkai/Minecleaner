package de.lunarakai.minecleaner.commands;

import java.util.Collection;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerArena;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.md_5.bungee.api.ChatColor;

public class ListCommand extends SubCommand{

    private final MinecleanerPlugin plugin;

    public ListCommand(MinecleanerPlugin plugin) {
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
        return MinecleanerPlugin.PERMISSION_ADMIN;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        sender.sendMessage(ChatColor.YELLOW + "Angelegte "+ plugin.getDisplayedPluginName() + "-Arenen");
        boolean any = false;
        for(MinecleanerArena arena : plugin.getArenaList().getArenas()) {
            Location location = arena.getLocation();
            sender.sendMessage(ChatColor.GRAY + "  " + arena.getName() + " @ " + location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ());
            any = true;
        }
        if(!any) {
            sender.sendMessage(ChatColor.GRAY + "  (keine)");
        }
        return true;
    }
    
    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
