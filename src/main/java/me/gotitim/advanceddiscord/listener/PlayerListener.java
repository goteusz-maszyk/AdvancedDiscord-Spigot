package me.gotitim.advanceddiscord.listener;

import me.gotitim.advanceddiscord.AdvancedDiscord;
import me.gotitim.advanceddiscord.Discord;
import me.gotitim.advanceddiscord.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerAdvancementDoneEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {
    private void send(String type, Player player, Placeholders placeholders) {
        ConfigurationSection msgData = AdvancedDiscord.getInstance().getConfig().getConfigurationSection("to-discord." + type);
        if(msgData == null) {
            Bukkit.getLogger().warning("Discord " + type + " message data not found!");
            return;
        }
        if(!Discord.sendMessageParsable(msgData, player, placeholders)) Bukkit.getLogger().warning("Failed to parse discord " + type + " message!");
    }

    @EventHandler
    public void onAdvancement(PlayerAdvancementDoneEvent event) {
        if(event.getAdvancement().getDisplay() == null) return;
        if(
                AdvancedDiscord.getInstance().getConfig().getString("to-discord.advancement.ignore").equalsIgnoreCase("announce-chat")
                && !event.getAdvancement().getDisplay().shouldAnnounceChat()
        ) return;
        send("advancement", event.getPlayer(), new Placeholders().set(event.getAdvancement().getDisplay()));
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        String message = event.getDeathMessage() == null ? "" : event.getDeathMessage();
        send("death", event.getEntity(), new Placeholders()
                .set("message", message)
                .set("message-noname", message.replace(event.getEntity().getDisplayName(), ""))
        );
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        send("chat", event.getPlayer(), new Placeholders().set("message", event.getMessage()));
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        send("join", event.getPlayer(), new Placeholders());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        send("quit", event.getPlayer(), new Placeholders());
    }
}
