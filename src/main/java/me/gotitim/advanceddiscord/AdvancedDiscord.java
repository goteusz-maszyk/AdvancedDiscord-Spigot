package me.gotitim.advanceddiscord;

import com.google.gson.JsonElement;
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
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import static com.google.gson.JsonParser.parseReader;

public final class AdvancedDiscord extends JavaPlugin {
    private static JDA bot;
    private static AdvancedDiscord instance;
    private WhitelistConfig whitelistConfig;

    public static JDA getBot() {
        return bot;
    }

    @Override
    public void onEnable() {
        instance = this;
        whitelistConfig = WhitelistConfig.setup(this);

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
        return whitelistConfig.getStringList("names").contains(player.getName())
                || whitelistConfig.getStringList("uuids").contains(player.getUniqueId().toString());
    }

    public boolean isWhitelisted(@NonNull Pair<String, UUID> data) {
        return whitelistConfig.getStringList("names").contains(data.getLeft())
                || whitelistConfig.getStringList("uuids").contains(data.getRight() == null ? null : data.getRight().toString());
    }

    public @Nullable Pair<String, UUID> fetchPlayer(String name) {
        JsonObject resJson = httpGetJson("https://api.mojang.com/users/profiles/minecraft/" + name);
        if(resJson == null) {
            return null;
        }
        if(resJson.get("errorMessage") != null) {
            return null;
        }
        String realNick = resJson.get("name").getAsString();
        BigInteger bi1 = new BigInteger(resJson.get("id").getAsString().substring(0, 16), 16);
        BigInteger bi2 = new BigInteger(resJson.get("id").getAsString().substring(16, 32), 16);
        UUID uuid = new UUID(bi1.longValue(), bi2.longValue());

        return new ImmutablePair<>(realNick, uuid);
    }

    public Pair<String, UUID> fetchPlayer(UUID uuid) {
        JsonObject resJson = httpGetJson("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid);
        if(resJson == null) return null;
        JsonElement realNick = resJson.get("name");
        return new ImmutablePair<>(realNick == null ? null : realNick.getAsString(), uuid);
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

    public void addWhiteList(Pair<String, UUID> playerData) {
        List<String> names = whitelistConfig.getStringList("names");
        List<String> uuids = whitelistConfig.getStringList("uuids");

        if(!names.contains(playerData.getLeft())) {
            names.add(playerData.getLeft());
        }
        if (playerData.getRight() != null && !uuids.contains(playerData.getRight().toString())) {
            uuids.add(playerData.getRight().toString());
        }

        whitelistConfig.set("names", names);
        whitelistConfig.set("uuids", uuids);
    }

    public void removeWhitelist(Pair<String, UUID> playerData) {
        List<String> names = whitelistConfig.getStringList("names");
        List<String> uuids = whitelistConfig.getStringList("uuids");

        names.remove(playerData.getLeft());
        uuids.remove(playerData.getRight().toString());

        whitelistConfig.set("names", names);
        whitelistConfig.set("uuids", uuids);
    }
}
