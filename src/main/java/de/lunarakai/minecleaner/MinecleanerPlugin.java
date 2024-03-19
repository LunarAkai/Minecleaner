package de.lunarakai.minecleaner;

import org.bukkit.plugin.java.JavaPlugin;

public final class MinecleanerPlugin extends JavaPlugin {


    // ------------------------------
    // TODO: start a new game (via ui)
    // TODO: For testing purposes -> write coords of cell into chat
    //  Format: Cell(X,Y) - CellType: Type
    // ------------------------------

    public static final String PERMISSION_PLAY = "minecleaner.play";
    public static final String PERMISSION_ADMIN = "minecleaner.admin";

    private MinecleanerManager minecleanerManager;
    private ArenaList arenaList;

    @Override
    public void onEnable() {
        // Plugin startup logic

        arenaList = new ArenaList(this);
        arenaList.load();

        //CommandRouter minecleanerCommand = new CommandRouter(getCommand("minecleaner"));
        //minecleanerCommand.addCommandMapping(new CreateCommand(this), "create");
        //minecleanerCommand.addCommandMapping(new DeleteCommand(this), "delete");
        //minecleanerCommand.addCommandMapping(new ListCommand(this), "list");



        //Test Commands
        //minecleanerCommand.addCommandMapping(new TestCommand(this), "testSingle");
        //minecleanerCommand.addCommandMapping(new TestArrayCommand(this), "testArray");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public ArenaList getArenaList() {
        return arenaList;
    }

    public MinecleanerManager getManager() {
        return minecleanerManager;
    }
}
