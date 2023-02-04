package me.gotitim.advanceddiscord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.exceptions.InsufficientPermissionException;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.function.Consumer;

public class Discord {
    private static TextChannel channel;

    public static void sendMessage(String message, Consumer<Message> onSend) {
        try {
            channel.sendMessage(message).queue(onSend);
        } catch (InsufficientPermissionException e) {
            AdvancedDiscord.getInstance().getLogger().warning("Failed to send message to discord: Missing Permission " + e.getPermission().getName());
        } catch (IllegalArgumentException e) {
            AdvancedDiscord.getInstance().getLogger().warning("Something has tried to send an empty message to discord!");
            e.printStackTrace();
        }
    }

    public static void sendMessage(String message) {
        sendMessage(message, msg -> {});
    }

    public static void sendMessage(MessageEmbed embed, Consumer<Message> onSend) {
        try {
            channel.sendMessageEmbeds(embed).queue(onSend);
        } catch (InsufficientPermissionException e) {
            AdvancedDiscord.getInstance().getLogger().warning("Failed to send message to discord: Missing Permission " + e.getPermission().getName());
        } catch (IllegalArgumentException e) {
            AdvancedDiscord.getInstance().getLogger().warning("Something has tried to send an empty message to discord!");
            e.printStackTrace();
        }
    }

    public static void sendMessage(MessageEmbed embed) { sendMessage(embed, msg -> {}); }

    public static TextChannel getChannel() {
        return channel;
    }

    public static void setChannel(TextChannel channel) {
        Discord.channel = channel;
    }

    /**
     * @param data Parsable message data from config
     * @return true if parsing succeeded or false if parsing failed and the message wasn't sent
     */
    public static boolean sendMessageParsable(@NotNull ConfigurationSection data, Player player, Placeholders ph) {
        if(data.getBoolean("ignore")) return true;

        ph.set(player);

        String content = data.getString("content");

        if (content == null) {
            EmbedBuilder embed = new EmbedBuilder()
                    .setColor(getColor(data.getString("embed-color")))
                    .setAuthor(
                            ph.parse(data.getString("embed-author-name")),
                            ph.parse(data.getString("embed-author-url")),
                            ph.parse(data.getString("embed-author-icon"))
                    )
                    .setTitle(ph.parse(data.getString("embed-title")))
                    .setDescription(ph.parse(data.getString("embed-description")))
                    .setFooter(ph.parse(data.getString("embed-footer")));
            sendMessage(embed.build());
            return false;
        } else {
            sendMessage(ph.parse(content));
        }

        return true;
    }

    @Nullable
    private static Color getColor(@Nullable String colorData) {
        if(colorData == null) return null;
        String[] splitted = colorData.split(",");
        try {
            return new Color(
                    Integer.parseInt(splitted[0]),
                    Integer.parseInt(splitted[1]),
                    Integer.parseInt(splitted[2])
            );
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
