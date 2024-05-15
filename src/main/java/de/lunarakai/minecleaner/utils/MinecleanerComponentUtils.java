package de.lunarakai.minecleaner.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;

public class MinecleanerComponentUtils {

    public static TextComponent createLangComponent(String langKey, NamedTextColor namedColor) {
        return Component.text()
                .color(namedColor)
                .append(Component.translatable(langKey))
                .build();
    }

    public static TextComponent createLangComponent(String langKey, String arg0, NamedTextColor namedColor) {
        return Component.text()
                .color(namedColor)
                .append(Component.translatable(langKey, Component.text(arg0)))
                .build();
    }

    public static TextComponent createLangComponent(String langKey, String arg0, String arg1, NamedTextColor namedColor) {
        return Component.text()
                .color(namedColor)
                .append(Component.translatable(langKey, Component.text(arg0), Component.text(arg1)))
                .build();
    }

    public static TextComponent createLangComponent(String langKey, String filler, String arg1, NamedTextColor namedColor, NamedTextColor namedColorArg1) {
        if(MinecleanerStringUtil.isValidURL(arg1)) {
            TextComponent urlMessage = Component.text(arg1);
            urlMessage = urlMessage.clickEvent(ClickEvent.clickEvent(ClickEvent.Action.OPEN_URL, arg1));
            urlMessage = urlMessage.color(namedColorArg1);

            return Component.text()
                    .color(namedColor)
                    .append(Component.translatable(langKey))
                    .append(Component.text(filler))
                    .append(urlMessage)
                    .build();
        } else {
            return Component.text()
                    .color(namedColor)
                    .append(Component.translatable(langKey))
                    .append(Component.text(filler))
                    .append(Component.text(arg1, namedColorArg1))
                    .build();
        }
    }
}
