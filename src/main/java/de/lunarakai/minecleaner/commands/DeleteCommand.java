package de.lunarakai.minecleaner.commands;

import java.util.Collection;
import java.util.List;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import de.lunarakai.minecleaner.MinecleanerArena;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.md_5.bungee.api.ChatColor;

public class DeleteCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public DeleteCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<name>";
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
        Player player = (Player) sender;
        MinecleanerArena arena = plugin.getArenaList().getArenaAtBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN));
        if(arena == null) {
            Block target = player.getTargetBlockExact(6);
            if(target != null) {
                arena = plugin.getArenaList().getArenaAtBlock(target);
            }
        }
        if(arena != null) {
            plugin.getArenaList().removeArena(arena);
            sender.sendMessage(ChatColor.YELLOW + "Die Minecleaner-Arena " + arena.getName() + " wurde gelöscht.");
        } else {
            sender.sendMessage(ChatColor.YELLOW + "Hier befindet sich keine Minecleaner-Arena.");
        }
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return List.of();
    }
    
}
