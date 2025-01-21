package de.lunarakai.minecleaner.utils;

import de.lunarakai.minecleaner.MinecleanerPlugin;
import org.bukkit.entity.Player;

public class MinecleanerUtils {

    public static boolean isPlayerInGroup(MinecleanerPlugin plugin, Player player) {
        return plugin.getGroupManager().getGroup(player) != null;
    }
}
