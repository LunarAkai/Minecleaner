package de.lunarakai.minecleaner;

import de.lunarakai.minecleaner.utils.ChatUtils;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MinecleanerGroupManager {

    public class MinecleanerGroup {

        public UUID owner;
        public HashSet<UUID> players;
        public Set<UUID> invitedPlayers;

        public MinecleanerGroup(UUID owner){
            this.owner = owner;
            this.players = new HashSet<>();
            this.invitedPlayers = new HashSet<>();

            players.add(owner);
        }

        public UUID getOwner() {
            return owner;
        }

        public Set<UUID> getInvitedPlayers() {
            return invitedPlayers;
        }

        public HashSet<UUID> getPlayers() {
            return players;
        }

        public void addPlayerToGroup(Player player) {
            UUID playerUUID = player.getUniqueId();

            if(!isPlayerInvited(playerUUID)) {
                return;
            }

            invitedPlayers.remove(playerUUID);
            players.add(playerUUID);
        }

        public void removePlayerFromGroup(Player player) {
            UUID playerUUID = player.getUniqueId();
            if(getOwner() == playerUUID) {
                for(Iterator<UUID> iterator = getGroup(player).getPlayers().iterator(); iterator.hasNext();) {
                    if(getOwner() == iterator.next()) {
                        continue;
                    }
                    Player iteratorPlayer = Bukkit.getPlayer(iterator.next());
                    ChatUtils.sendSimpleInfoMessage(iteratorPlayer, "group.creator.removed");
                }
                deleteGroup(getGroup(Bukkit.getPlayer(owner)));
            }
            players.remove(playerUUID);
            if(players.size() < 2) {
                ChatUtils.sendSimpleInfoMessage(Bukkit.getPlayer(owner), "group.dismantled.alone");
                deleteGroup(getGroup(Bukkit.getPlayer(owner)));
            }
        }

        public boolean isPlayerInvited(UUID playerUUID) {
            return invitedPlayers.contains(playerUUID);
        }

        public boolean isInGroup(UUID playerUUID) {
            return players.contains(playerUUID) || owner.equals(playerUUID);
        }

        public boolean invitePlayerToGroup(Player player) {
            UUID playerUUID = player.getUniqueId();
            if(isPlayerInvited(playerUUID) || isInGroup(playerUUID))
                return false;

            invitedPlayers.add(player.getUniqueId());
            return true;
        }

        public void removePlayerFromInvitedList(Player player) {
            UUID playerUUID = player.getUniqueId();
            if(!isPlayerInvited(playerUUID)) {
                return;
            }
            invitedPlayers.remove(playerUUID);
        }
    }

    private final Set<MinecleanerGroup> groups;

    public MinecleanerGroupManager() {
        this.groups = new HashSet<>();
    }

    public void createGroup(Player player) {

        if (getGroup(player) != null) {
            return;
        }
        groups.add(new MinecleanerGroup(player.getUniqueId()));
    }

    public MinecleanerGroup getGroup(Player player){
        UUID playerUUID = player.getUniqueId();
        for (MinecleanerGroup group : groups) {
            if (group.isInGroup(playerUUID)) {
                return group;
            }
        }
        return null;
    }

    /**
     *  loops through all MinecleanerGroups to check if the player is invited to any group
     *
     * @param player    Minecraft Player
     * @return          the MinecleanerGroup the Player is invited to or null
     */
    public MinecleanerGroup getInvitedGroup(Player player){
        UUID playerUUID = player.getUniqueId();
        for (MinecleanerGroup group : groups) {
            if (group.isPlayerInvited(playerUUID)) {
                return group;
            }
        }
        return null;
    }

    public void deleteGroup(MinecleanerGroup minecleanerGroup) {
        groups.remove(minecleanerGroup);
    }

    /**
     * removes all MinecleanerGroups
     */
    public void deleteAllGroups() {
        groups.clear();
    }

    /**
     * Returns the size of the MinecleanerGroup the player is in
     *
     * @param player    the Minecraft Player
     * @return          the size of the group the player is in
     */
    public int getGroupSize(Player player) {
        return getGroup(player).getPlayers().size();
    }
}
