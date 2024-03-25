package de.lunarakai.minecleaner.utils;

import java.util.Arrays;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.destroystokyo.paper.profile.ProfileProperty;

public enum MinecleanerHeads {
    MINESWEEPER_TILE_0("38206373-5653-4431-85aa-6276f3f9a046", "Minesweeper Tile 0", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzRjMjg0YTRlOTc0MDA1ZWE4ZDFkNGQwNjc0ZWMwODk0ZWZkOGY2ZGQwMjQ4NjM5YTZjZmE5NGY4NTM4OCJ9fX0="),
    MINESWEEPER_TILE_1("dafc2272-0615-4a1d-ac16-06deeffe81c3", "Minesweeper Tile 1", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjdmYWM3MWUzNmQ1MGExYWQyZTJjMTBlMzJlMGM1OWVlZGVmYjBkMzU0NDZhOGZiNDg0ODEzOGZlMjZmYzkifX19"),
    MINESWEEPER_TILE_2("79db3710-fa22-4e53-8058-e15fb60333ee", "Minesweeper Tile 2", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYjQ5Njg5NTViZDk0ODcwODA0MWUxMmIxNjRhZGZjZGI0NjM5OWMyZDM4MTA1OWVkNzFjZTc0YWUyNGY4ZGYifX19"),
    MINESWEEPER_TILE_3("f09ec6e9-9bd2-4d4f-b99e-049fa173f9a7", "Minesweeper Tile 3", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWY2NzhjZmNjNGVlYjI1OWQ4ZTU3YjI2ZjRmNmEzNWE3NmQ3NDM3MDQzYmU2YzIzYWU4NTVjNDdjOGEyZTkifX19"),
    MINESWEEPER_TILE_4("ce566d7a-0e6e-4fb4-8d16-2a372500e427", "Minesweeper Tile 4", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmI2OTcxYWJiMThiY2E5ZmFmYWFkOWVkNWExNTk2MTMyYmVjY2ZmYjg4YTMxYzgyOTMyMGM4NjdlZTQ3NyJ9fX0="),
    MINESWEEPER_TILE_5("f3c15345-3bc1-4bf5-ba40-7f110a4d45e6", "Minesweeper Tile 5", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzYyMDZlNDc2MTRjOGY5ODM0MTNkZWUzMzJmMmYzMmU4ZGEzN2ZhNTdjNGNlYmExZDE0YjE2NDNiMjU5NTcifX19"),
    MINESWEEPER_TILE_6("c7cfbda5-4039-417f-8e30-173658315b0d", "Minesweeper Tile 6", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNjZmOTdlNTYzZDg1ZGM0ZDczODcxZDRjZGZjYzI2ZDhjZDQ0ZTg5ZmFmYjE1MDRjOGQ5YTJhYzVhNTZjIn19fQ=="),
    MINESWEEPER_TILE_7("4d5502a9-8edd-4815-af45-5110124b9f08", "Minesweeper Tile 7", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODdlZjYxODVhZGQ0MTk3MzU3OTNjOGMyYTg0N2Q5YzRlMzkxYTJjNWI5YjJlYzI2MmNlYTk1NTc1YjBkMCJ9fX0="),
    MINESWEEPER_TILE_8("168c915e-87c9-4c14-a297-3aafa692d3a5", "Minesweeper Tile 8", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODI3MWNkZDM4ZThhN2M3NDIzMWFmOGExNTU2MThmNGZmY2I3ZjkxN2U4ODI2YzJiM2MxODM2ZDFiZDExNmQzIn19fQ=="),
    MINESWEEPER_TILE_FLAG("ae508256-8113-463c-adce-877bad2227c0", "Minesweeper Tile Flag", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzg0YTdmY2IyNDc0MDZlMzUzYTM2ZTU1NmFkMTk1NzhjM2ViZTRlMTU1ODFkYjEwNmQxNWE1Y2I5ZGFkIn19fQ=="),
    MINESWEEPER_TILE_UNKNOWN("2fe94bff-c5c1-410e-8c99-cf713c850930", "Minesweeper Tile Unknown", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYzdmMWVhMjZlYTVlNjg1YjJmMmY4NzY0OTAxYmU5MTRmZTM1NTU5Y2IxZWNiMWVmMzRiN2U0NmFiYzhlZTU0MCJ9fX0="),
    TNT("22a627e8-e68a-4fca-ad91-7d14c36e8556", "Minesweeper TNT", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZGVlYmI4YjAzY2QyN2QzNDM1ZTExNTYxNmI4ZWQzNWRjYjQyN2FmNWIwYjFjYzUyNmQzMjY1YTcyZDQ5M2UifX19"),
    EXPLODED("166a5608-5206-49bb-a325-fbcd9564dc2b", "Minesweeper Exploded", "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjgzMWQ1NzIxOTUwZWZhOWFhNzk5NjdlYjE5MTZhYmViNjRjOTc4ZWE4NTkzYTBjNjgzMzU0ODA5YzZjMzYxZCJ9fX0="),

    MINESWEEPER_LETTER_M("a9046a9a-cf3a-4171-971d-6752561c1962", "Minesweeper Letter M", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTZmZTljNTlhNGE2ZTg3MzRlZjFmMGIzNWFkNzFjNjM1ZjY2OTMzYzcxOWEwMjA4NGYxZjc1YTY0MzgyYzJhNCIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_I("3c3409c6-7671-4103-b2cf-42340c8689c1", "Minesweeper Letter I", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMzQxYjYxNzFhNjM5MmZkMTU0NDNhN2M5Y2E2ZjEwOGE1OGM3ZmUyZDgxNDZiN2VmZTk3OTQ0ODQxNTJhMWQ5ZCIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_N("015bd450-d99a-41ac-a313-5fc7eb68dad6", "Minesweeper Letter N", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNTFkZGEwY2MwZTQwNzM1MTQ0MmVlNTE4MGIzMGYwMzZmNWM3ODI5MTk5NGNmYTgwZjc3ZTQyNmI1NjM1MmM2MiIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_E("dd3290e5-b62b-4e3e-977a-39d44c782ce1", "Minesweeper Letter E", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjRlOTg4MDQ3ZWQwYTFlZDFkZjdhZTFlM2ViZDAyZTQ2ZmY5YWM5NGIzYmEwYTY2OWMzNDExMDRjNDY2NTYwMiIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_S("9ac8c7b4-e62e-40f6-b03a-29ee70fe3788", "Minesweeper Letter S", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZTExMTI3NmU0ZjU0MDdhNzc1ZWNmNTRlZGRiMzM4Y2VjYTI0Yzg1MmY0OGYzMjQ3NDkzZTU4YzU4MDEwZmE3ZCIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_W("6d580f6c-11ce-4af5-ac20-b5214a0bd901", "Minesweeper Letter W", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjk2MjZiOTg2NWE0OGE4MGU3YzVlMTY0MmZkZWUzMDczN2IzMGE0MjcyYWJiNWE3ZTc5Njc5MjFiODRmNmFhZiIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_P("922fb986-d434-4509-9bef-2ba1c3af753c", "Minesweeper Letter P", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmIyNmNlODI4ZmE5Y2JlMzBlYWIwMDVhNGQzNzA3YzVmZGY0Yzg0MWI0ZGJlNjA5OGZhN2MzMGE2YWUzZGIwYSIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_R("3eacba28-ad1a-446a-beb4-824f545353d0", "Minesweeper Letter R", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvZjdjOWUwYzFkNzBlNDIzNzI0Y2QwZjJlN2IwNDM1N2I4MDVmZTNmZWM5NzJkN2U4NDRmMDZhNjJjODYzNGU1MiIKICAgIH0KICB9Cn0="),
    MINESWEEPER_LETTER_MINUS("7f15a38b-fae8-4b0a-908f-93f72fea0c5f", "Minesweeper Letter Minus", "ewogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNzcxYzNkOWQwZGUzODQ4Mjg3MGY0MTlmMGRiYWFiZjM4NGVlODFmZTI4Y2EzMTA5ZWQyYmMwOTM0NzIyYWFjNiIKICAgIH0KICB9Cn0="),
    ;
    
    private ItemStack head;
 
    private MinecleanerHeads(String ownerUUIDString, String ownerName, String texturesProperty) {
        head = createHead(UUID.fromString(ownerUUIDString), ownerName, texturesProperty);
    }
 
    public ItemStack getHead() {
        return new ItemStack(head);
    }
 
    public ItemStack getHead(String displayName) {
        return getHead(displayName, (String[]) null);
    }
 
    public ItemStack getHead(String displayName, String... lore) {
        ItemStack stack = getHead();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(displayName);
        if (lore != null && lore.length > 0) {
            meta.setLore(Arrays.asList(lore));
        }
        stack.setItemMeta(meta);
        return stack;
    }
 
    public static ItemStack createHead(UUID ownerUUID, String ownerName, String texturesProperty) {
        if (ownerName == null) {
            ownerName = ownerUUID.toString().substring(0, 16);
        }
        ItemStack stack = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        PlayerProfile profile = Bukkit.createProfile(ownerUUID, ownerName);
        profile.setProperty(new ProfileProperty("textures", texturesProperty));
        meta.setPlayerProfile(profile);
        stack.setItemMeta(meta);
        return stack;
    }
    
}
