package me.gotitim.advanceddiscord;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

public class WhitelistConfig extends YamlConfiguration {
    private final File file;
    private boolean loaded;

    private WhitelistConfig(File file) {
        this.file = file;
        this.loaded = false;
    }

    public static WhitelistConfig setup(AdvancedDiscord plugin) {
        File file = new File(plugin.getDataFolder(), "whitelist.yml");
        WhitelistConfig config = new WhitelistConfig(file);

        config.setup();
        return config;
    }

    protected void setup() {
        if(!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Bukkit.getLogger().severe("Failed to create " + file.getName() + " config file!");
                e.printStackTrace();
            }
        }
        reload();
    }

    public void save() {
        if(!loaded) return;
        try {
            save(file);
        } catch (IOException e) {
            Bukkit.getLogger().severe("Failed to save " + file.getName() + " config file!");
            e.printStackTrace();
        }
    }

    public void reload() {
        try {
            loaded = false;
            load(file);
            loaded = true;
        } catch (IOException | InvalidConfigurationException e) {
            Bukkit.getLogger().severe("Failed to load " + file.getName() + " config file!");
            e.printStackTrace();
        }
    }

    @Override
    public void set(@NotNull String path, @Nullable Object value) {
        super.set(path, value);
        save();
    }
}
