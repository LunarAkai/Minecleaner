package de.lunarakai.minecleaner;

import de.iani.cubesideutils.bukkit.inventory.AbstractWindow;
import de.lunarakai.minecleaner.utils.ItemUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;


public class MinecleanerSettingsInventory extends AbstractWindow {

    private static final int SETTINGS_ALLOW_MANUEL_RESET = 10;
    private static final int SETTINGS_ADDITIONAL_DISPLAY = 12;
    private static final int SETTINGS_TIMER = 14;
    private static final int SETTINGS_RESETTIME = 16;
    private static final int WINDOW_SIZE = 27;

    private MinecleanerPlugin plugin;

    public MinecleanerSettingsInventory(Player player, MinecleanerPlugin plugin) {
        super(player, Bukkit.createInventory(player, InventoryType.CHEST, plugin.getDisplayedPluginName() + " Einstellungen"));
        this.plugin = plugin;
    }

    @Override
    protected void rebuildInventory() {
        Player player = getPlayer();

        for (int i = 0; i < WINDOW_SIZE; i++) {
            ItemStack item;
            switch (i) {
                case SETTINGS_ALLOW_MANUEL_RESET -> {
                    if (plugin.getManager().getSettingsValue("allowmanualreset", player) == 0) {
                        item = ItemUtil.createGuiItem(Material.SHEARS, Component.translatable("settings.manualreset.deny", NamedTextColor.RED));
                    } else {
                        item = ItemUtil.createGuiItem(Material.SHEARS, Component.translatable("settings.manualreset.allow", NamedTextColor.GREEN));
                    }
                }
                case SETTINGS_ADDITIONAL_DISPLAY -> {
                    if (plugin.getManager().getSettingsValue("additionaldisplay", player) == 0) {
                        item = ItemUtil.createGuiItem(Material.NAME_TAG, Component.translatable("settings.additionaltimer.deny", NamedTextColor.RED));
                    } else {
                        item = ItemUtil.createGuiItem(Material.NAME_TAG, Component.translatable("settings.additionaltimer.allow", NamedTextColor.GREEN));
                    }
                }
                case SETTINGS_TIMER -> {
                    if (plugin.getManager().getSettingsValue("timer", player) == 0) {
                        item = ItemUtil.createGuiItem(Material.CLOCK, Component.translatable("settings.timer.deny", NamedTextColor.RED));
                    } else {
                        item = ItemUtil.createGuiItem(Material.CLOCK, Component.translatable("settings.timer.allow", NamedTextColor.GREEN));
                    }
                }
                case SETTINGS_RESETTIME -> {
                    int current = plugin.getManager().getSettingsValue("resettime", player);
                    item = ItemUtil.createGuiItem(Material.CANDLE, Component.translatable("settings.resettime.text", NamedTextColor.GOLD).append(Component.text(": ")).append(Component.text(current, NamedTextColor.RED)).append(Component.text(" s", NamedTextColor.RED)));
                }
                default -> item = ItemUtil.EMPTY_ICON;
            }
            this.getInventory().setItem(i, item);
        }
    }

    @Override
    public void onItemClicked(InventoryClickEvent event) {
        if (!mayAffectThisInventory(event)) {
            return;
        }

        event.setCancelled(true);
        if (!getInventory().equals(event.getClickedInventory())) {
            return;
        }

        Player player = getPlayer();


        int slot = event.getSlot();
        switch (slot) {
            case SETTINGS_ALLOW_MANUEL_RESET -> {
                if(plugin.getManager().getSettingsValue("allowmanualreset", player) == 0) {
                    plugin.getManager().updateSettingsValue("allowmanualreset", 1, player);
                } else {
                    plugin.getManager().updateSettingsValue("allowmanualreset", 0, player);
                }
                rebuildInventory();
            }
            case SETTINGS_ADDITIONAL_DISPLAY -> {
                if(plugin.getManager().getSettingsValue("additionaldisplay", player) == 0) {
                    plugin.getManager().updateSettingsValue("additionaldisplay", 1, player);
                } else {
                    plugin.getManager().updateSettingsValue("additionaldisplay", 0, player);
                }
                rebuildInventory();
            }
            case SETTINGS_TIMER -> {
                if(plugin.getManager().getSettingsValue("timer", player) == 0) {
                    plugin.getManager().updateSettingsValue("timer", 1, player);
                } else {
                    MinecleanerArena arena = plugin.getArenaList().getPlayerArena(player);
                    plugin.getManager().updateSettingsValue("timer", 0, player);
                    if(arena != null) {
                        arena.updateIngameInfoTexts();
                    }
                }
                rebuildInventory();
            }
            case SETTINGS_RESETTIME -> {
                int current = plugin.getManager().getSettingsValue("resettime", player);
                if(plugin.getManager().getSettingsValue("resettime", player) < 10) {
                    plugin.getManager().updateSettingsValue("resettime", current + 1, player);
                } else {
                    plugin.getManager().updateSettingsValue("resettime", 1, player);
                }
                rebuildInventory();
            }
            default -> {
            }
        }
    }
}
