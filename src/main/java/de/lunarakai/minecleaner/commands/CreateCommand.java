package de.lunarakai.minecleaner.commands;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nullable;

import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
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

import static de.lunarakai.minecleaner.utils.MinecleanerComponentUtils.createLangComponent;

public class CreateCommand extends SubCommand {
    private static final Pattern VALID_ARENA_NAME = Pattern.compile("^[a-z0-9_]+$");

    private final MinecleanerPlugin plugin;
    
    public CreateCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<name> [noblocks] [widthindex]";
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
    public boolean onCommand(CommandSender sender, Command command, String arg2, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        final Player player = (Player) sender;
        if(args.remaining() < 1 || args.remaining() > 3) {
            sender.sendMessage(ChatColor.DARK_RED + commandString + getUsage());
            return true;
        }
        String name = args.getNext().toLowerCase().trim();
        if(!VALID_ARENA_NAME.matcher(name).matches()) {
            sender.sendMessage(createLangComponent("arena.name.invalid", NamedTextColor.DARK_RED));
            return true;
        }
        if(plugin.getArenaList().getArena(name) != null) {
            sender.sendMessage(createLangComponent( "arena.name.exists", NamedTextColor.DARK_RED));
            return true;
        }
        boolean noblocks = false;
        int widthindex = 0;

        while(args.hasNext()) {
            String arg = args.getNext().toLowerCase().trim();
            if(arg.equals("noblocks")) {
                noblocks = true;
            } else if(arg.matches("^-?\\d+$")) {
                try {
                    widthindex = Integer.parseInt(arg);
                } catch(NumberFormatException e) {
                    sender.sendMessage(createLangComponent("arena.widthindex.invalid", NamedTextColor.DARK_RED));
                    sender.sendMessage(createLangComponent("arena.widthindex.validOptions", NamedTextColor.DARK_RED));
                    return true;
                }
                if(widthindex > 3) {
                    sender.sendMessage(createLangComponent( "arena.widthindex.toolarge", NamedTextColor.DARK_RED));
                    sender.sendMessage(createLangComponent("arena.widthindex.validOptions", NamedTextColor.DARK_RED));
                    return true;
                }
            } else {
                sender.sendMessage(ChatColor.DARK_RED + commandString + getUsage());
                return true;
            }
        }

        BlockFace orientation = null;
        Location location = null;

        @Nullable
        RayTraceResult target = player.rayTraceBlocks(6);
        if(target == null || target.getHitBlock() == null) {
            //sender.sendMessage(ChatColor.DARK_RED + "Bitte gucke den Block an, der im Zentrum des " + plugin.getDisplayedPluginName() + "-Spielfelds sein soll.");
            sender.sendMessage(createLangComponent( "arena.create.lookAtCenter", plugin.getDisplayedPluginName(), NamedTextColor.DARK_RED));
            return true;
        }
        BlockFace face = target.getHitBlockFace();
        if(face != BlockFace.NORTH && face != BlockFace.WEST && face != BlockFace.EAST && face != BlockFace.SOUTH) {
            //sender.sendMessage(ChatColor.DARK_RED + "Bitte gucke die Seite des Blockes an, wo das " + plugin.getDisplayedPluginName() + "-Spielfeld erstellt werden soll.");
            sender.sendMessage(createLangComponent("arena.create.lookAtSide", plugin.getDisplayedPluginName(), NamedTextColor.DARK_RED));
            return true;
        }
        location = target.getHitBlock().getLocation();
        orientation = face;

        MinecleanerArena newArena = new MinecleanerArena(plugin, name, location, widthindex, orientation);
        if(plugin.getArenaList().collidesWithArena(newArena)) {
            sender.sendMessage(createLangComponent("arena.create.otherArena", NamedTextColor.DARK_RED));
            return true;
        }
        newArena.generateBlockDisplays();
        if(!noblocks) {
            newArena.generateBackgroundBlocks();
        }
        plugin.getArenaList().addArena(newArena);
        sender.sendMessage(createLangComponent("arena.create.success", NamedTextColor.GREEN));
        return true;
    }

    @Override
    public Collection<String> onTabComplete(CommandSender sender, Command command, String alias, ArgsParser args) {
        if(args.remaining() == 2 || args.remaining() == 3) {
            args.getNext();

            boolean noblocks = false;
            int widthindex = 0;
            while(args.remaining() > 1) {
                String arg = args.getNext().toLowerCase().trim();
                if(arg.equals("noblocks")) {
                    noblocks = true;
                } else if(arg.matches("^-?\\d+$")) {
                    try {
                        widthindex = Integer.parseInt(arg);
                    } catch(NumberFormatException e) {
                    }
                } else {
                    return List.of();
                }
            }
            ArrayList<String> result = new ArrayList<>();
            result.add("");
            if(!noblocks) {
                result.add("noblocks");
            }
            if(widthindex != 0 || widthindex != 1 || widthindex != 2) {
                result.add("widthindex");
            }
            return result;
        }
        return List.of();
    }
}
