package de.lunarakai.minecleaner;

import java.util.Iterator;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;
import net.md_5.bungee.api.ChatColor;

public class MinecleanerListener implements Listener {
    private final MinecleanerPlugin plugin;

    public MinecleanerListener(MinecleanerPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST) 
    public void onPlayerInteract(PlayerInteractEvent e) {
        if(e.getHand() != EquipmentSlot.HAND) return;
        if((e.getAction() == Action.LEFT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_BLOCK)) {
            Block block = e.getClickedBlock();
            MinecleanerArena arena = plugin.getArenaList().getPlayerArena(e.getPlayer());
            if(arena != null) {
                e.setCancelled(true);
                MinecleanerArena arenaClicked = plugin.getArenaList().getArenaAtBlock(block);

                if(arenaClicked != arena) {
                    return;
                }

                boolean hasRightClicked = false;
                if(e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                    hasRightClicked = true;
                }

                if(arena.getCurrentMinecleanerGame() != null && !arena.getCurrentMinecleanerGame().gameover && (arena.getArenaStatus() == ArenaStatus.PLAYING || arena.getArenaStatus() == ArenaStatus.COMPLETED)) {
                    if(arena.getArenaStatus() == ArenaStatus.PLAYING) {
                        int d0x = arena.getOrientation().getModX();
                        int d0z = arena.getOrientation().getModZ();
                        int d1x = -d0z;
                        int d1z = d0x;

                        if (e.getBlockFace() == arena.getOrientation()) {

                            Player player = e.getPlayer();
                            RayTraceResult r2 = player.rayTraceBlocks(36.0);

                            if(r2 != null) {
                                Vector hitPos = r2.getHitPosition();
                                Vector substract = new Vector(0.5, 0.5, 0.5);

                                Location loc = hitPos.subtract(arena.getLocation().toVector()).subtract(substract).toLocation(player.getWorld()); //(0.5, 0.5, 0.5); // substract 0.5, 0.5, 0.5
                                double lx = loc.getX();
                                double ly = loc.getY();
                                double lz = loc.getZ();
                                double dy = ly + 1.5;
                                double dz = -d1x * lx - d1z * lz + 1.5;

                                double blockx = (dy / 3.0) * 9.0;
                                double blockz = (dz / 3.0) * 9.0;

                                int blockxInt = (int) blockx;
                                int blockzInt = (int) blockz;
                                blockx -= blockxInt;
                                blockz -= blockzInt;

                                if(blockzInt < arena.getArenaWidth() && blockxInt < arena.getArenaHeight()) {
                                    plugin.getManager().handleFieldClick(e.getPlayer(), blockzInt, blockxInt, hasRightClicked);
                                }
                                //player.sendMessage("Arena click! " + blockxInt + " " + blockzInt + " Right Clicked: " + hasRightClicked);
                            }
                        }
                    }
                } else if(arena.hasPlayer() && arena.getArenaStatus() == ArenaStatus.COMPLETED && !hasRightClicked && (plugin.getManager().getSettingsValue("allowmanualreset", e.getPlayer()) == 1)) {
                    plugin.getManager().getSchedulerGameOver().cancel();
                    plugin.getManager().leaveArena(arenaClicked.getCurrentPlayer(), false);
                }
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
    public void onEntityDamage(EntityDamageEvent e) {
        if(plugin.getArenaList().getArenaForBlockDisplay(e.getEntity().getUniqueId()) != null) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInventoryClick(InventoryClickEvent e) {
        if(e.getWhoClicked() instanceof Player player) {
            MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
            if(arena != null) {
                if(e.getInventory().equals(plugin.getManager().getConfirmPlayingInventory())) {
                    e.setCancelled(true);
                    if(arena.getArenaStatus() == ArenaStatus.CONFIRM_PLAYING) {
                        int slot = e.getRawSlot();
                        boolean hasConfirmed = slot == 1 ? true : false;
                        if(hasConfirmed) {  
                            plugin.getManager().startGame(player);
                            //player.closeInventory();
                        }
                        player.closeInventory();
                    }
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInventoryClose(InventoryCloseEvent e) {
        if(e.getPlayer() instanceof Player player) {
            MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
            if(arena != null) {
                if(arena.getArenaStatus() == ArenaStatus.CONFIRM_PLAYING && e.getInventory().equals(plugin.getManager().getConfirmPlayingInventory())) {
                    plugin.getManager().leaveArena(player, false);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
        if(arena != null) {
            if(arena.isTooFarAway(player)) {
                player.sendMessage(ChatColor.YELLOW + "Du hast dich zu weit von der Arena entfernt. Das Spiel wurde abgebrochen.");
                plugin.getManager().leaveArena(player, false);
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

    @EventHandler
    public void onBlockBurn(BlockBurnEvent e) {
        if(plugin.getArenaList().getArenaAtBlock(e.getBlock()) != null) {
            e.setCancelled(true);
            return;
        }
    }

    @EventHandler
    public void onBlockPistonExtend(BlockPistonExtendEvent e) {
        Iterator<Block> it = e.getBlocks().iterator();
        while(it.hasNext()) {
            Block block = it.next();
            if(plugin.getArenaList().getArenaAtBlock(block) != null) {
                e.setCancelled(true);
                return;
            }
        } 
    }

    @EventHandler
    public void onBlockPistonRetract(BlockPistonRetractEvent e) {
        Iterator<Block> it = e.getBlocks().iterator();
        while(it.hasNext()) {
            Block block = it.next();
            if(plugin.getArenaList().getArenaAtBlock(block) != null) {
                e.setCancelled(true);
                return;
            }
        } 
    }
}


