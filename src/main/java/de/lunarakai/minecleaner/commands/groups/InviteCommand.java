package de.lunarakai.minecleaner.commands.groups;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerGroupManager;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
            sender.sendMessage(Component.text(commandString + getUsage(), NamedTextColor.DARK_RED));
            return true;
        }
        String playerName = args.getNext().trim();
        Player invitedPlayer = plugin.getServer().getPlayer(playerName);

        if(invitedPlayer == player) {
            player.sendMessage(Component.text("Du kannst dich nicht selber in eine Gruppe einladen.", NamedTextColor.DARK_RED));
            return true;
        }

        MinecleanerGroupManager groupManager = plugin.getGroupManager();
        if(groupManager.getInvitedGroup(player) != null) {
            player.sendMessage(Component.text("Du wurdest bereits in eine Gruppe eingeladen. Bitte kümmere dich zuerst um die Einladung bevor du eine eigene Gruppe erstellst.", NamedTextColor.YELLOW));
            return true;
        }

        if(groupManager.getGroup(player) != null && Bukkit.getPlayer(groupManager.getGroup(player).getOwner()).equals(player)) {
            player.sendMessage(Component.text("Nur als Ersteller der Gruppe bist du berechtigt Leute einzuladen.", NamedTextColor.YELLOW));
            return true;
        }

        if(groupManager.getGroup(player) == null) {
            groupManager.createGroup(player);
        }

        assert invitedPlayer != null;
        player.sendMessage(Component.text("Du hast " + invitedPlayer.getName() + " in eine " + plugin.getDisplayedPluginName() + "-Gruppe eingeladen", NamedTextColor.GREEN));
        invitedPlayer.sendMessage(Component.text("Du wurdest von " + player.getName() + " in eine " + plugin.getDisplayedPluginName() + "-Gruppe eingeladen.", NamedTextColor.GREEN));
        groupManager.getGroup(player).invitePlayerToGroup(invitedPlayer);

        return true;
    }
}
