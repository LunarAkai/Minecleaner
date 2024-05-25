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
import de.lunarakai.minecleaner.utils.ChatUtils;
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

        if(plugin.getArenaList().getPlayerArena(player) != null) {
            ChatUtils.sendSimpleWarningMessage(player, "Du kannst keine Einladung verschicken während du in einer Runde bist.");
            return true;
        }

        if(args.remaining() < 1 || args.remaining() >= 2) {
            ChatUtils.sendSimpleWarningMessage(player, commandString + getUsage());
            return true;
        }
        String playerName = args.getNext().trim();
        Player invitedPlayer = plugin.getServer().getPlayer(playerName);

        if(invitedPlayer == null) {
            ChatUtils.sendSimpleWarningMessage(player, "Du kannst keine Person einladen, die entweder offline oder auf einen anderen Server ist.");
            return true;
        }

        if(invitedPlayer == player) {
            ChatUtils.sendSimpleWarningMessage(player, "Du kannst dich nicht selber in eine Gruppe einladen.");
            return true;
        }

        if(plugin.getArenaList().getPlayerArena(invitedPlayer) != null) {
            ChatUtils.sendSimpleWarningMessage(player, "Du kannst Spieler nicht einladen, die bereits in einer Runde sind.");
            return true;
        }

        MinecleanerGroupManager groupManager = plugin.getGroupManager();
        if(groupManager.getInvitedGroup(player) != null) {
            ChatUtils.sendSimpleInfoMessage(player, "Du wurdest bereits in eine Gruppe eingeladen. Bitte kümmere dich zuerst um die Einladung bevor du eine eigene Gruppe erstellst.");
            return true;
        }

        if(groupManager.getGroup(player) != null && !Bukkit.getPlayer(groupManager.getGroup(player).getOwner()).equals(player)) {
            ChatUtils.sendSimpleInfoMessage(player, "Nur als Ersteller der Gruppe bist du berechtigt Leute einzuladen.");
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
