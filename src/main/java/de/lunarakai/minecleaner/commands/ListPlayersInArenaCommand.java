package de.lunarakai.minecleaner.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.*;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerArena;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ListPlayersInArenaCommand extends SubCommand {
    private final MinecleanerPlugin plugin;

    public ListPlayersInArenaCommand(MinecleanerPlugin plugin) {
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
    public boolean onCommand(CommandSender sender, Command command, String s, String s1, ArgsParser argsParser) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        MinecleanerArena arena = plugin.getArenaList().getArenaAtBlock(player.getLocation().getBlock().getRelative(BlockFace.DOWN));
        if(arena == null) {
            Block target = player.getTargetBlockExact(6);
            if(target != null) {
                arena = plugin.getArenaList().getArenaAtBlock(target);
            }
        }
        if(arena != null) {
           player.sendMessage(Component.text("Players in Arena: " + Arrays.toString(arena.getCurrentPlayers()), NamedTextColor.GRAY));
        } else {
            player.sendMessage(Component.text("Hier befindet sich keine Arena.", NamedTextColor.GRAY));
        }
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        return List.of();
    }
}
