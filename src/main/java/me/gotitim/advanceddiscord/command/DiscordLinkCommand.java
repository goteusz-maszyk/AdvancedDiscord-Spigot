package me.gotitim.advanceddiscord.command;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;

import java.awt.*;
import java.io.FileReader;
import java.io.IOException;
import java.util.UUID;

import static com.google.gson.JsonParser.parseReader;

public class NickCommand {
    public void exec(SlashCommandInteractionEvent event) {
        String nick = event.getOption("nick").getAsString();
        // ADD TO WHITELIST

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api.mojang.com/users/profiles/minecraft/" + nick)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            event.reply("500 Internal Server Error (see server log for more)").queue();
            return;
        }

        JsonObject resJson = parseReader(response.body().charStream()).getAsJsonObject();
        String realNick = resJson.get("name").getAsString();
        String uuid = resJson.get("id").getAsString();

        EmbedBuilder embed = new EmbedBuilder()
                .setTitle(realNick)
                .setDescription("Dodano do whitelisty")
                .setColor(new Color(255, 255, 255))
                .setThumbnail("https://crafatar.com/renders/head/" + uuid + "?overlay");
        event.replyEmbeds(embed.build()).queue();
    }
}
