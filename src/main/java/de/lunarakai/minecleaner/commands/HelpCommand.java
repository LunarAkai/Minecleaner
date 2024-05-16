package de.lunarakai.minecleaner.commands;

import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class HelpCommand extends SubCommand {
    MinecleanerPlugin plugin;

    public HelpCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "[(empty)|group]";
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
    public boolean onCommand(CommandSender commandSender, Command command, String s, String commandString, ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException, NoPermissionException, IllegalSyntaxException, InternalCommandException {
        Player player = (Player) commandSender;

        String subMenu = "";
        if(args.remaining() == 1) {
             subMenu = args.getNext().toLowerCase().trim();
        }

        if(subMenu.equals("group")) {
            showHelpGroup(player);
            return true;
        } else {
            showGeneralHelp(player);
            return true;
        }
    }

    private void showGeneralHelp(Player player) {
        player.sendMessage(Component.text("--- " + plugin.getDisplayedPluginName() + " Help ---", NamedTextColor.AQUA)
                .append(Component.newline())
                .append(Component.text("  /... info: ", NamedTextColor.BLUE))
                .append(Component.text("Allgemeine Auskunft zum Plugin", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /... stats [playername]: ", NamedTextColor.BLUE))
                .append(Component.text("Zeigt dir entweder deine eigenen Stats (leer lassen) oder die Stats anderer Spieler an", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /... settings: ", NamedTextColor.BLUE))
                .append(Component.text("Öffnet ein Menü in dem du Veränderungen an deinen eigenen Einstellungen für " + plugin.getDisplayedPluginName() + " vornehmen kannst", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /... help [group]: ", NamedTextColor.BLUE))
                .append(Component.text("Zeigt dieses Menü (frei lassen) oder die Hilfe für Gruppen an (group)", NamedTextColor.GREEN)));
    }

    private void showHelpGroup(Player player) {
        player.sendMessage(Component.text("--- " + plugin.getDisplayedPluginName() + " Group Help ---", NamedTextColor.AQUA)
                .append(Component.newline())
                .append(Component.text("  /... invite <playername>: ", NamedTextColor.BLUE))
                .append(Component.text("Lädt andere Spieler zu deiner " + plugin.getDisplayedPluginName() + " Gruppe ein", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /... accept | deny: ", NamedTextColor.BLUE))
                .append(Component.text("Nehme eine erhaltene Einladung an (accept), oder lehne sie ab (deny)", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /... dismantlegroup: ", NamedTextColor.BLUE))
                .append(Component.text("Löst die Gruppe, die du erstellt hast, auf", NamedTextColor.GREEN))
                .append(Component.newline())
                .append(Component.text("  /... groupmembers: ", NamedTextColor.BLUE))
                .append(Component.text("Listet die Mitglieder deiner Gruppe auf", NamedTextColor.GREEN)));
    }
}
