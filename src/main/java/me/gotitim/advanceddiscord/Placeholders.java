package me.gotitim.advanceddiscord;

import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.bukkit.ChatColor;
import org.bukkit.advancement.AdvancementDisplay;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Placeholders {
    private final Map<String, String> placeholders;
    public Placeholders() {
        placeholders = new HashMap<>();
    }

    public Placeholders set(String replace, String with) {
        placeholders.put("%" + replace + "%", with);
        return this;
    }
    public Placeholders set(AdvancementDisplay ad) {
        placeholders.put("%advancement-name%", ad.getTitle());
        placeholders.put("%advancement-description%", ad.getDescription());
        return this;
    }

    public void set(Player player) {
        set("player-display", player.getDisplayName());
        Pair<String, UUID> playerData = AdvancedDiscord.getInstance().fetchPlayer(player.getName());
        set("player-uuid", playerData == null ? player.getUniqueId().toString() : playerData.getRight().toString());
        set("player-world", player.getWorld().getName());
        set("player-dimension", player.getWorld().getEnvironment().name());
        set("player-ping", String.valueOf(player.getPing()));
        set("player-address", player.getAddress().toString());
    }

    @Nullable
    public String parse(@Nullable String text) {
        if(text == null) return null;
        final String[] returned = List.of(text).toArray(new String[]{});
        returned[0] = ChatColor.translateAlternateColorCodes('&', returned[0]);
        placeholders.forEach((replace, with) -> returned[0] = returned[0].replaceAll(replace, with));
        return returned[0];
    }
}
