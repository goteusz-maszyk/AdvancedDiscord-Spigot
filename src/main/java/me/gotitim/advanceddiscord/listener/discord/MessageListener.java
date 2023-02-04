package me.gotitim.advanceddiscord.listener.discord;

import me.gotitim.advanceddiscord.AdvancedDiscord;
import me.gotitim.advanceddiscord.Discord;
import me.gotitim.advanceddiscord.Placeholders;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import static me.gotitim.advanceddiscord.AdvancedDiscord.getBot;
import static me.gotitim.advanceddiscord.AdvancedDiscord.getConfigString;

public class MessageListener extends ListenerAdapter {
    @Override
    public void onReady(@NotNull ReadyEvent event) {
        AdvancedDiscord.getBot().setRequiredScopes("bot", "applications.commands");
        if(AdvancedDiscord.getBot().getGuilds().size() == 0) {
            AdvancedDiscord.getInstance().getLogger().log(Level.WARNING, "The bot is not added to any guild! You can invite it here: "
                    + AdvancedDiscord.getBot().getInviteUrl(
                        Permission.MESSAGE_SEND,
                        Permission.MESSAGE_EMBED_LINKS,
                        Permission.MESSAGE_ATTACH_FILES,
                        Permission.MESSAGE_HISTORY,
                        Permission.MESSAGE_ADD_REACTION,
                        Permission.CREATE_PRIVATE_THREADS,
                        Permission.CREATE_PUBLIC_THREADS,
                        Permission.VIEW_CHANNEL,
                        Permission.ADMINISTRATOR
            ));
            Bukkit.getServer().getPluginManager().disablePlugin(AdvancedDiscord.getInstance());
            return;
        }
        String channelId = AdvancedDiscord.getInstance().getConfig().getString("discord-channel");
        if(channelId == null) {
            AdvancedDiscord.getInstance().getLogger().log(Level.SEVERE, "Discord Channel not set! Disabling AdvancedDiscord.");
            Bukkit.getServer().getPluginManager().disablePlugin(AdvancedDiscord.getInstance());
            return;
        }
        Discord.setChannel(AdvancedDiscord.getBot().getTextChannelById(channelId));
        if(Discord.getChannel() == null) {
            AdvancedDiscord.getInstance().getLogger().log(Level.SEVERE, "Discord Channel not found! Disabling AdvancedDiscord.");
            Bukkit.getServer().getPluginManager().disablePlugin(AdvancedDiscord.getInstance());
        }
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if(event.getAuthor().getId().equals(getBot().getSelfUser().getId())) return;
        if(event.getMessage().isEdited() || event.getMessage().isPinned() || !event.getMessage().isFromGuild()) return;
        String msg = getConfigString("from-discord");
        if(msg == null) {
            AdvancedDiscord.getInstance().getLogger().warning("Discord channel message not found!");
            return;
        }
        Placeholders ph = new Placeholders();
        ph.set("sender-display", event.getMember().getEffectiveName());
        ph.set("channel-name", event.getChannel().getName());
        ph.set("sender-tag", event.getAuthor().getAsTag());
        ph.set("content", event.getMessage().getContentDisplay());

        List<String> stickerNames = new ArrayList<>();
        event.getMessage().getStickers().forEach((sticker) -> stickerNames.add(sticker.getName()));
        if(stickerNames.size() > 0) {
            ph.set("stickers", getConfigString("sticker-pattern")
                    .replace("%every-name%", String.join(getConfigString("sticker-spliterator"), stickerNames))
            );
        } else {
            ph.set("stickers", "");
        }

        List<String> attachmentNames = new ArrayList<>();
        event.getMessage().getAttachments().forEach((attachment) -> attachmentNames.add(attachment.getFileName()));
        if(attachmentNames.size() > 0) {
            ph.set("attachments", getConfigString("attachment-pattern")
                    .replace("%every-name%", String.join(getConfigString("attachment-spliterator"), attachmentNames))
            );
        } else {
            ph.set("attachments", "");
        }

        Bukkit.broadcastMessage(ph.parse(msg));
    }
}
