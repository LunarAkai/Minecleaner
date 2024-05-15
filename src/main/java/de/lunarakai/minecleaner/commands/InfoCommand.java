package de.lunarakai.minecleaner.commands;

import de.iani.cubesideutils.NamedChatColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import de.iani.cubesideutils.bukkit.commands.SubCommand;
import de.iani.cubesideutils.bukkit.commands.exceptions.DisallowsCommandBlockException;
import de.iani.cubesideutils.bukkit.commands.exceptions.IllegalSyntaxException;
import de.iani.cubesideutils.bukkit.commands.exceptions.InternalCommandException;
import de.iani.cubesideutils.bukkit.commands.exceptions.NoPermissionException;
import de.iani.cubesideutils.bukkit.commands.exceptions.RequiresPlayerException;
import de.iani.cubesideutils.commands.ArgsParser;
import de.lunarakai.minecleaner.MinecleanerPlugin;
import net.md_5.bungee.api.ChatColor;

import static de.lunarakai.minecleaner.utils.MinecleanerComponentUtils.createLangComponent;

public class InfoCommand extends SubCommand{
    MinecleanerPlugin plugin;

    public InfoCommand(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getUsage() {
        return "";
    }

    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public String getRequiredPermission() {
        return MinecleanerPlugin.PERMISSION_PLAY;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command arg1, String arg2, String arg3,
            ArgsParser args) throws DisallowsCommandBlockException, RequiresPlayerException,
            NoPermissionException, IllegalSyntaxException, InternalCommandException {
                sender.sendMessage(NamedChatColor.GREEN + "--- " + ChatColor.AQUA + plugin.getName() + ChatColor.GREEN + " ---");
                sender.sendMessage(createLangComponent("minecleaner.info.version", ": ", plugin.getPluginMeta().getVersion(), NamedTextColor.AQUA, NamedTextColor.GREEN));
                sender.sendMessage(createLangComponent("minecleaner.info.developer", ": ", plugin.getPluginMeta().getAuthors().get(0), NamedTextColor.AQUA, NamedTextColor.GREEN));
                sender.sendMessage(createLangComponent("minecleaner.info.website", ": ", plugin.getPluginMeta().getWebsite(), NamedTextColor.AQUA, NamedTextColor.GREEN));
                sender.sendMessage(createLangComponent("minecleaner.info.license", ": ", "GPL-3.0", NamedTextColor.AQUA, NamedTextColor.GREEN));
                return true;
    }
}
