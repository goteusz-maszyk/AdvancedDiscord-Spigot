package me.gotitim.advanceddiscord.command;

import me.gotitim.advanceddiscord.AdvancedDiscord;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class DynamicWhitelistCommand implements CommandExecutor {
    private final AdvancedDiscord plugin;

    public DynamicWhitelistCommand(AdvancedDiscord advancedDiscord) {
        this.plugin = advancedDiscord;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        String nameOrUUID = args[0];
        UUID playerUid;
        String playerName;
        try {
            playerUid = UUID.fromString(nameOrUUID);
            playerName = plugin.fetchPlayer(playerUid).getLeft();
        } catch (IllegalArgumentException e) {
            Pair<String, UUID> data = plugin.fetchPlayer(nameOrUUID);
            playerUid = data.getRight();
            playerName = data.getLeft();
        }
        if (plugin.isWhitelisted(new ImmutablePair<>(playerName, playerUid))) {
            plugin.whitelistNames.remove(playerName);
            plugin.whitelistUUIDs.remove(playerUid);
            sender.sendMessage("Un-Whitelisted " + playerName);
        } else {
            plugin.whitelistUUIDs.add(playerUid);
            plugin.whitelistNames.add(playerName);
            sender.sendMessage("Whitelisted " + playerName);
        }

        return false;
    }
}
