package de.lunarakai.minecleaner;

import org.bukkit.entity.Player;
import com.google.common.base.Preconditions;
import net.md_5.bungee.api.ChatColor;

public class MinecleanerManager {
    private final MinecleanerPlugin plugin;

    public MinecleanerManager(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void joinArena(Player player, MinecleanerArena arena) {
        if (!player.hasPermission(MinecleanerPlugin.PERMISSION_PLAY)) {
            return;
        }
        Preconditions.checkArgument(plugin.getArenaList().getPlayerArena(player) == null, "player is in an arena");
        Preconditions.checkArgument(arena.getArenaStatus() == ArenaStatus.INACTIVE, "arena is in use");
        arena.addJoiningPlayer(player);
        plugin.getArenaList().setArenaForPlayer(player, arena);
    }

    public void leaveArena(Player player, boolean message) {
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
        Preconditions.checkArgument(arena != null, "player is in no arena");
        arena.removePlayer();
        plugin.getArenaList().setArenaForPlayer(player, null);
        if(message) {
            player.sendMessage(ChatColor.YELLOW + "Das Minecleanerspiel wurde abgebrochen.");
        }
    }
}
