package me.gotitim.advanceddiscord.command;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.bukkit.Bukkit;

import java.util.stream.Stream;

public class DiscordListCommand {
    public void exec(SlashCommandInteractionEvent event) {
        Stream<String> nameList = Bukkit.getOnlinePlayers().stream().map(player -> player.getName());
        event.reply("There are " + Bukkit.getOnlinePlayers().size() + " of a max of " + Bukkit.getMaxPlayers() + " players online: \n"
            + String.join(", ", nameList.toList())).queue();
    }
}
