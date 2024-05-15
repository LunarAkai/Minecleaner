package de.lunarakai.minecleaner.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.*;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerGroup;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InviteCommand extends SubCommand {
    /*
        TODO:
            - Invite other Players to play in Duo Mode
            - Add Functionality to support multiple Players in the same game
            - use settings of player that invited the other player
     */

    private final MinecleanerPlugin plugin;

    public InviteCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "<Player>";
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
    public boolean onCommand(CommandSender sender, Command command, String s, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) sender;
        if(args.remaining() < 1 || args.remaining() >= 2) {
            sender.sendMessage(ChatColor.DARK_RED + commandString + getUsage());
            return true;
        }
        String playerName = args.getNext().trim();
        Player invitedPlayer = plugin.getServer().getPlayer(playerName);

        MinecleanerGroup group = new MinecleanerGroup();
        group.createGroup(player);
        UUID groupUUID = group.getGroupUUID(player);

        group.invitePlayerToGroup(groupUUID, invitedPlayer);

        return false;
    }
}
