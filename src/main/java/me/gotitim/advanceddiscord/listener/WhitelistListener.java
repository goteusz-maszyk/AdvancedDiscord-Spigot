package me.gotitim.advanceddiscord.listener;

import me.gotitim.advanceddiscord.AdvancedDiscord;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import yt.szczurek.dynamicwhitelist.api.WhitelistCheckEvent;

public final class WhitelistListener implements Listener {
    private final AdvancedDiscord plugin;

    public WhitelistListener(AdvancedDiscord advancedDiscord) {
        this.plugin = advancedDiscord;
    }

    @EventHandler
    public void onWhitelistCheck(WhitelistCheckEvent event) {
        if (plugin.isWhitelisted(event.getPlayer())) event.setAllowed(true);
    }
}