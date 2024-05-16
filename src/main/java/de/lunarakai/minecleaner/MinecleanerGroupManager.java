package de.lunarakai.minecleaner;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
                    iteratorPlayer.sendMessage(Component.text("Die Gruppe wurde aufgelöst, da die Person, welche die Gruppe erstellt hat, aus der Gruppe entfernt wurde.", NamedTextColor.YELLOW));

                }
                deleteGroup(getGroup(Bukkit.getPlayer(owner)));
            }
            players.remove(playerUUID);
            if(players.size() < 2) {
                Bukkit.getPlayer(owner).sendMessage(Component.text("Die Gruppe wurde aufgelöst, da du nur noch alleine in der Gruppe bist", NamedTextColor.YELLOW));
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

    private UUID groupCreator;
    private final Set<MinecleanerGroup> groups;

    public MinecleanerGroupManager() {
        this.groupCreator = null;
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

    public void deleteAllGroups() {
        groups.clear();
    }
}
