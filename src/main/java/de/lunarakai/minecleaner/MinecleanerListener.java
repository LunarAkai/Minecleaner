package de.lunarakai.minecleaner;

import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import net.md_5.bungee.api.ChatColor;

public class MinecleanerListener implements Listener {
    private final MinecleanerPlugin plugin;

    public MinecleanerListener(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST) 
    public void onPlayerInteract(PlayerInteractEvent e) {
        if((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Block block = e.getClickedBlock();
            MinecleanerArena arena = plugin.getArenaList().getPlayerArena(e.getPlayer());
            if(arena != null) {

                // TODO

            } else {
                arena = plugin.getArenaList().getArenaAtBlock(block);
                if(arena != null) {
                    e.setCancelled(true);
                    if(e.getHand() == EquipmentSlot.HAND) {
                        if(arena.getArenaStatus() == ArenaStatus.INACTIVE) {
                            plugin.getManager().joinArena(e.getPlayer(), arena);
                        } else {
                            e.getPlayer().sendMessage(ChatColor.YELLOW + "Hier spielt schon jemand anderes");
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(e.getPlayer());
        if(arena != null) {
            plugin.getManager().leaveArena(e.getPlayer(), false);
        }
    }
    
}
