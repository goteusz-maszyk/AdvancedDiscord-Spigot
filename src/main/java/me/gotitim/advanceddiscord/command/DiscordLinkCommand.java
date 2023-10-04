package me.gotitim.advanceddiscord.command;

import me.gotitim.advanceddiscord.AdvancedDiscord;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.internal.utils.tuple.Pair;

import java.awt.*;
import java.util.UUID;

public class DiscordLinkCommand {
    public void exec(SlashCommandInteractionEvent event) {
        String nick = event.getOption("nick").getAsString();

        Pair<String, UUID> playerData = AdvancedDiscord.getInstance().fetchPlayer(nick);

        AdvancedDiscord.getInstance().addWhiteList(playerData);

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(playerData.getLeft())
                .setDescription("Dodano do whitelisty")
                .setColor(new Color(255, 255, 255))
                .setThumbnail("https://crafatar.com/renders/head/" + playerData.getRight() + "?overlay");
        event.replyEmbeds(embed.build()).queue();
    }
}
