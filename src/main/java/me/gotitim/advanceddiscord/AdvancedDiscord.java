package me.gotitim.advanceddiscord;

import com.google.gson.JsonObject;
import me.gotitim.advanceddiscord.command.DynamicWhitelistCommand;
import me.gotitim.advanceddiscord.listener.DiscordListener;
import me.gotitim.advanceddiscord.listener.PlayerListener;
import me.gotitim.advanceddiscord.listener.WhitelistListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.profile.PlayerProfile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static com.google.gson.JsonParser.parseReader;

public final class AdvancedDiscord extends JavaPlugin {
    private static JDA bot;
    private static AdvancedDiscord instance;

    public final List<String> whitelistNames = new ArrayList<>();
    public final List<UUID> whitelistUUIDs = new ArrayList<>();
    public static JDA getBot() {
        return bot;
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        String token = getConfig().getString("auth-token");
        try {
            bot = JDABuilder.createDefault(token,
                            GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_MESSAGE_REACTIONS, GatewayIntent.MESSAGE_CONTENT,
                            GatewayIntent.GUILD_EMOJIS_AND_STICKERS)
                    .disableCache(CacheFlag.VOICE_STATE, CacheFlag.SCHEDULED_EVENTS).build();
        } catch (Exception e) {
            getLogger().log(Level.SEVERE, "No token provided, disabling plugin!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        bot.addEventListener(new DiscordListener());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        getServer().getPluginManager().registerEvents(new WhitelistListener(this), this);

        getCommand("dynlist").setExecutor(new DynamicWhitelistCommand(this));
    }

    @Override
    public void onDisable() {
        bot.shutdownNow();
    }

    public static AdvancedDiscord getInstance() {
        return instance;
    }
    public static String getConfigString(String key) {
        if(key == null) return "";
        return AdvancedDiscord.getInstance().getConfig().getString(key, "");
    }

    public boolean isWhitelisted(Player player) {
        return whitelistNames.contains(player.getName()) || whitelistUUIDs.contains(player.getUniqueId());
    }

    public boolean isWhitelisted(Pair<String, UUID> data) {
        return whitelistNames.contains(data.getLeft()) || whitelistUUIDs.contains(data.getRight());
    }

    public Pair<String, UUID> fetchPlayer(String name) {
        JsonObject resJson = httpGetJson("https://api.mojang.com/users/profiles/minecraft/" + name);

        String realNick = resJson.get("name").getAsString();
        String uuid = resJson.get("id").getAsString();

        return new ImmutablePair<>(realNick, UUID.fromString(uuid));
    }

    public Pair<String, UUID> fetchPlayer(UUID uuid) {
        String realNick = httpGetJson("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid).get("name").getAsString();
        return new ImmutablePair<>(realNick, uuid);
    }

    public JsonObject httpGetJson(String url) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();
        Response response;
        try {
            response = client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        return parseReader(response.body().charStream()).getAsJsonObject();
    }
}
