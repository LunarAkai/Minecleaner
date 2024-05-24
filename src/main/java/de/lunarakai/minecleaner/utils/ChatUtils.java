package de.lunarakai.minecleaner.utils;

import java.util.regex.Pattern;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

public class ChatUtils {
    static Pattern langKeyPattern = Pattern.compile("^\\w+\\.\\w+\\.\\w+$");

    public static void sendSingleLineSuccessMessage(Player player, String message) {
        if(message.matches(langKeyPattern.pattern())) {
            player.sendMessage(Component.translatable(message, NamedTextColor.GREEN));
        } else {
            player.sendMessage(Component.text(message, NamedTextColor.GREEN));
        }
    }

    public static void sendSingleLineInfoMessage(Player player, String message) {
        if(message.matches(langKeyPattern.pattern())) {
            player.sendMessage(Component.translatable(message, NamedTextColor.GOLD));
        } else {
            player.sendMessage(Component.text(message, NamedTextColor.GOLD));
        }
    }

    public static void sendSingleLineWarningMessage(Player player, String message) {
        if(message.matches(langKeyPattern.pattern())) {
            player.sendMessage(Component.translatable(message, NamedTextColor.DARK_RED));
        } else {
            player.sendMessage(Component.text(message, NamedTextColor.DARK_RED));
        }
    }

    public static void sendSingleLineSpecialMessage(Player player, String message, NamedTextColor color) {
        if(message.matches(langKeyPattern.pattern())) {
            player.sendMessage(Component.translatable(message, color));
        } else {
            player.sendMessage(Component.text(message, color));
        }
    }
}
