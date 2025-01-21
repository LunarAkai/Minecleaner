package de.lunarakai.minecleaner;

/**
 *  Possible Status of a MinecleanerArena
 *  <li>{@link #INACTIVE}</li>
 *  <li>{@link #CONFIRM_PLAYING}</li>
 *  <li>{@link #PLAYING}</li>
 *  <li>{@link #COMPLETED}</li>
 *
 * @see MinecleanerArena
 *
 */
public enum ArenaStatus {
    /**
     * default state
     */
    INACTIVE,

    /**
     * Active when the player (or group leader) has interacted with an {@link #INACTIVE} arena and
     * sees the confirmPlayingInventory to decide if they start a Game or not
     * @see MinecleanerManager#getConfirmPlayingInventory()
     */
    CONFIRM_PLAYING,

    /**
     * Active when a Player (or Group) has confirmed to start a Minecleaner Game (and while playing)
     */
    PLAYING,

    /**
     * Active when a Player (or Group) has either won or lost a Mineclenaer Game and the Arena the scheduled for a reset -> {@link #INACTIVE}
     */
    COMPLETED
}
