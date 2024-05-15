package de.lunarakai.minecleaner.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.*;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class InviteCommand extends SubCommand {
    /*
        TODO:
            - Invite other Players to play in Duo Mode
            - Add Functionality to support multiple Players in the same game
            - use settings of player that invited the other player
     */

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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String s1, ArgsParser argsParser) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        return false;
    }
}
