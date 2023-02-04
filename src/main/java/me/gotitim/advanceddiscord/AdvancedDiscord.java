package me.gotitim.advanceddiscord;

import me.gotitim.advanceddiscord.listener.PlayerListener;
import me.gotitim.advanceddiscord.listener.discord.MessageListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Level;

public final class AdvancedDiscord extends JavaPlugin {
    private static JDA bot;
    private static AdvancedDiscord instance;

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
        bot.addEventListener(new MessageListener());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
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
}
