package de.lunarakai.minecleaner;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class MinecleanerGroup {
    private final Player groupCreator;
    private final ArrayList<UUID> groups;
    private final HashMap<UUID, UUID> playerGroupMap;
    private final HashMap<UUID, UUID> invitedPlayerGroupMap;

    private int groupSize;

    public MinecleanerGroup() {
        this.groupCreator = null;
        this.groups = new ArrayList<>();
        this.playerGroupMap = new HashMap<>();
        this.invitedPlayerGroupMap = new HashMap<>();
    }

    public void createGroup(Player player) {
        UUID groupUUID = UUID.randomUUID();
        groups.add(groupUUID);
        playerGroupMap.put(player.getUniqueId(), groupUUID);
        groupSize++;
    }

    private void deleteGroup(UUID groupUUID) {
        groups.remove(groupUUID);
    }

    public void addPlayerToGroup(Player player) {

    }

    public void invitePlayerToGroup(UUID groupUUID, Player player) {
        invitedPlayerGroupMap.put(groupUUID, player.getUniqueId());
        player.sendMessage("You have been invited. :)");
    }

    private void removePlayerFromGroup(Player player) {
        playerGroupMap.remove(player.getUniqueId());
    }

    public void removePlayerFromInvitedMap(UUID playerUUID) {
        invitedPlayerGroupMap.remove(playerUUID);
    }

    public UUID getGroupUUID(Player player) {
        return playerGroupMap.get(player.getUniqueId());
    }

    public MinecleanerGroup getGroup(Player player) {
        return
    }

    public Player getGroupCreator(UUID groupUUID) {
        return groupCreator;
    }
    public boolean isInGroup(Player player) {
        // TODO
    }

    public boolean isInvited(Player player) {
        return invitedPlayerGroupMap.containsKey(player.getUniqueId());
    }

    public int getGroupSize() {
        return groupSize;
    }
}
