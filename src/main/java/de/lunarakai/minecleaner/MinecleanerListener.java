package de.lunarakai.minecleaner;

import de.lunarakai.minecleaner.utils.ChatUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Bukkit;
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
                } else if(arena.hasPlayers() && arena.getArenaStatus() == ArenaStatus.COMPLETED && !hasRightClicked && (plugin.getManager().getSettingsValue("allowmanualreset", e.getPlayer()) == 1)) {
                    plugin.getManager().getSchedulerGameOver().cancel();
                    plugin.getLogger().log(Level.INFO, "canceled reset for arena " + arena.getName() + " loc: " + arena.getLocation());
                    plugin.getManager().leaveArena(arenaClicked.getCurrentPlayers(), false, true);
                }
            } else {
                arena = plugin.getArenaList().getArenaAtBlock(block);
                if(arena != null) {
                    e.setCancelled(true);
                    if(e.getHand() == EquipmentSlot.HAND) {
                        if(arena.getArenaStatus() == ArenaStatus.INACTIVE) {
                            int arraySize = 1;
                            if(plugin.getGroupManager().getGroup(e.getPlayer()) != null) {
                                arraySize = plugin.getGroupManager().getGroup(e.getPlayer()).getPlayers().size();
                            }

                            if(plugin.getGroupManager().getGroup(e.getPlayer()) != null && plugin.getGroupManager().getGroup(e.getPlayer()).getOwner() != e.getPlayer().getUniqueId()) {
                                ChatUtils.sendSimpleWarningMessage(e.getPlayer(), "group.game.nopermission");
                                return;
                            }
                            Player[] players = new Player[arraySize];

                            if(plugin.getGroupManager().getGroup(e.getPlayer()) != null) {
                                int i = 0;
                                for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(e.getPlayer()).getPlayers().iterator(); iterator.hasNext();) {
                                    Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                                    players[i] = iteratorPlayer;
                                    i++;
                                }
                            } else {
                                Arrays.fill(players, e.getPlayer());
                            }
                            plugin.getManager().joinArena(players, arena);
                        } else {
                            ChatUtils.sendSimpleInfoMessage(e.getPlayer(), "Hier spielt schon jemand anderes");
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
                            Player[] players;
                            if(plugin.getGroupManager().getGroup(player) != null) {
                                players = new Player[plugin.getGroupManager().getGroupSize(player)];
                                players[0] = player;
                            } else {
                                players = new Player[1];
                                players[0] = player;
                            }
                            plugin.getManager().startGame(players);

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
                    int arraySize = plugin.getGroupManager().getGroup(player) != null ? plugin.getGroupManager().getGroupSize(player) : 1;
                    Player[] players = new Player[arraySize];

                    if(plugin.getGroupManager().getGroup(player) != null) {
                        for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
                            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                            Arrays.fill(players, iteratorPlayer);
                        }
                    } else {
                        Arrays.fill(players, player);
                    }
                    plugin.getManager().leaveArena(players, false, true);
                }
            }
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        final Player player = e.getPlayer();
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
        if(arena != null) {
            if(plugin.getGroupManager().getGroup(player) == null) {
                if((arena.isTooFarAway(player))) {
                    ChatUtils.sendSimpleInfoMessage(player, "arena.common.toofaraway");
                    Player[] players = new Player[] {
                            player
                    };
                    plugin.getManager().leaveArena(players, false, true);
                }
            } else {
                Player ownerPlayer = Bukkit.getPlayer(plugin.getGroupManager().getGroup(player).getOwner());
                if(ownerPlayer.equals(player)) {
                    if(arena.isTooFarAway(ownerPlayer)) {

                        for(Iterator<UUID> iterator = plugin.getGroupManager().getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
                            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                            if(iteratorPlayer == ownerPlayer) {
                                ChatUtils.sendSimpleInfoMessage(iteratorPlayer, "arena.common.toofaraway");
                                continue;
                            }
                            assert iteratorPlayer != null;
                            ChatUtils.sendSimpleInfoMessage(iteratorPlayer, "arena.common.groupleadertoofaraway");
                        }
                        Player[] players = new Player[] {
                                ownerPlayer
                        };
                        plugin.getManager().leaveArena(players, false, true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        MinecleanerGroupManager groupManager = plugin.getGroupManager();
        MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);

        if(arena != null) {
            if(groupManager.getGroup(player) != null) {
                MinecleanerGroupManager.MinecleanerGroup group = groupManager.getGroup(player);
                Player ownerPlayer = Bukkit.getPlayer(group.getOwner());
                if(player == ownerPlayer) {
                    Player[] players = iterateOverGroupMembersOnCreatorPlayerQuit(player, groupManager, group);
                    plugin.getManager().leaveArena(players, false, true);
                } else {
                    iterateOverGroupMembersOnPlayerQuit(player, group);
                }
            } else {
                Player[] players = new Player[] {
                        e.getPlayer()
                };
                plugin.getManager().leaveArena(players, false, true);
            }
        } else  {
            if(groupManager.getGroup(player) != null) {
                MinecleanerGroupManager.MinecleanerGroup group = groupManager.getGroup(player);
                Player ownerPlayer = Bukkit.getPlayer(group.getOwner());
                if(player == ownerPlayer) {
                    iterateOverGroupMembersOnCreatorPlayerQuit(player, groupManager, group);
                } else {
                    iterateOverGroupMembersOnPlayerQuit(player, group);
                }
            } else {
                return;
            }
        }
    }

    private void iterateOverGroupMembersOnPlayerQuit(Player player, MinecleanerGroupManager.MinecleanerGroup group) {
        Player[] players = new Player[group.players.size()];
        int i = 0;
        for(Iterator<UUID> iterator = group.getPlayers().iterator(); iterator.hasNext();) {
            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
            if(iteratorPlayer == player) {
                i++;
                continue;
            }
            players[i] = iteratorPlayer;
            ChatUtils.sendSimpleInfoMessage(iteratorPlayer, player.getName() + " hat den Server verlassen und wurde aus der Gruppe entfernt.");
        }
        group.removePlayerFromGroup(player);
    }

    private Player[] iterateOverGroupMembersOnCreatorPlayerQuit(Player player, MinecleanerGroupManager groupManager, MinecleanerGroupManager.MinecleanerGroup group) {
        Player[] players = new Player[group.players.size()];
        int i = 0;
        for(Iterator<UUID> iterator = group.getPlayers().iterator(); iterator.hasNext();) {
            Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
            if(iteratorPlayer == player) {
                i++;
                continue;
            }
            players[i] = iteratorPlayer;
            ChatUtils.sendSimpleInfoMessage(iteratorPlayer, "Die " + plugin.getDisplayedPluginName() + "gruppe in der du dich befindest wurde aufgelöst. Die Person, welche die Gruppe erstellt hat, hat den Server verlassen");
        }
        groupManager.deleteGroup(group);
        return players;
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


