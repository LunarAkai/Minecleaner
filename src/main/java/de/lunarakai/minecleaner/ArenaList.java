package de.lunarakai.minecleaner;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class ArenaList {
    private static final String ARENAS_FILENAME = "mcl_arenas.yml";
    private final MinecleanerPlugin plugin;
    private File arenaFile;
    private final HashMap<String, MinecleanerArena> arenas;

    private final HashMap<UUID, MinecleanerArena> playersInArena;
    private final HashMap<Location, MinecleanerArena> arenaBlocks;
    private final HashMap<UUID, MinecleanerArena> arenaBlockDisplays;

    public ArenaList(MinecleanerPlugin plugin) {
        this.plugin = plugin;
        this.arenas = new HashMap<>();
        this.arenaBlocks = new HashMap<>();
        this.playersInArena = new HashMap<>();
        this.arenaBlockDisplays = new HashMap<>();
        this.arenaFile = new File(plugin.getDataFolder(), ARENAS_FILENAME);
    }

    public void load() {
        arenas.clear();
        if(!this.arenaFile.isFile()) {
            return;
        }

        YamlConfiguration conf = new YamlConfiguration();
        try {
            conf.load(this.arenaFile);
        } catch(IOException | InvalidConfigurationException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not load arenas file", e); 
        }
        ConfigurationSection arenasSection = conf.getConfigurationSection("arenas");
        if(arenasSection != null) {
            for(String arenaName : arenasSection.getKeys(false)) {
                ConfigurationSection arenaSection = arenasSection.getConfigurationSection(arenaName);
                if(arenaSection != null) {
                    MinecleanerArena arena = new MinecleanerArena(plugin, arenaSection);
                    this.arenas.put(arena.getName(), arena);
                    setArenaBlocks(arena);
                }
            }
        }
    }

    public void save() {
        YamlConfiguration conf = new YamlConfiguration();
        ConfigurationSection arenaSection = conf.createSection("arenas");
        int i = 0;
        for(MinecleanerArena arena : this.arenas.values()) {
            arena.save(arenaSection.createSection(Integer.toString(i++)));
        }
        this.arenaFile.getParentFile().mkdirs();
        try {
            conf.save(this.arenaFile);
        } catch(IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not save arenas file", e);
        }
    }

    private void setArenaBlocks(MinecleanerArena arena) {
        for(Location loc : arena.getBlocks()) {
            arenaBlocks.put(loc.clone(), arena);
        }
        for(UUID id : arena.getBlockDisplays()) {
            if(id != null) {
                arenaBlockDisplays.put(id, arena);
            }
        }
    }

    public MinecleanerArena getArena(String name) {
        return arenas.get(name);
    }

    public Collection<MinecleanerArena> getArenas() {
        return arenas.values();
    }

    public void addArena(MinecleanerArena arena) {
        this.arenas.put(arena.getName(), arena);
        setArenaBlocks(arena);
        save();
    }

    public boolean collidesWithArena(MinecleanerArena newArena) {
        for(Location location : newArena.getBlocks()) {
            if(arenaBlocks.get(location) != null) {
                return true;
            }
        }
        return false;
    }

    public void setArenaForPlayer(Player player, MinecleanerArena arena) {
        if(arena != null) {
            playersInArena.put(player.getUniqueId(), arena);
        } else {
            playersInArena.remove(player.getUniqueId());
        }
    }

    public MinecleanerArena getPlayerArena(Player player) {
        return playersInArena.get(player.getUniqueId());
    }

    public MinecleanerArena getArenaAtBlock(Block block) {
        return arenaBlocks.get(block.getLocation());
    }

    public MinecleanerArena getArenaForBlockDisplay(UUID id) {
        return arenaBlockDisplays.get(id);
    }

    public void removeArena(MinecleanerArena arena) {
        if(arena.hasPlayer()) {
            plugin.getManager().leaveArena(arena.getCurrentPlayer(), true);
        }
        
        for(UUID id : arena.getBlockDisplays()) { // TODO
            if(id != null) {
                arenaBlockDisplays.remove(id);
            }
        }
        for(Location block : arena.getBlocks()) { // TODO
            arenaBlocks.remove(block);
        }
        arena.removeBlockDisplays(); // TODO

        arenas.remove(arena.getName());
        save();
    }
} 
