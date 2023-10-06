package me.gotitim.advanceddiscord.command;

import me.gotitim.advanceddiscord.AdvancedDiscord;
import net.dv8tion.jda.internal.utils.tuple.ImmutablePair;
import net.dv8tion.jda.internal.utils.tuple.Pair;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.math.BigInteger;
import java.util.Objects;
import java.util.UUID;

public class DynamicWhitelistCommand implements CommandExecutor {
    private final AdvancedDiscord plugin;

    public DynamicWhitelistCommand(AdvancedDiscord advancedDiscord) {
        this.plugin = advancedDiscord;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            sender.sendMessage("USAGE: /dynlist <player>");
            return false;
        }
        String nameOrUUID = args[0];
        UUID playerUid;
        String playerName;
        try {
            BigInteger bi1 = new BigInteger(nameOrUUID.substring(0, 16), 16);
            BigInteger bi2 = new BigInteger(nameOrUUID.substring(16, 32), 16);
            playerUid = new UUID(bi1.longValue(), bi2.longValue());
            playerName = Objects.requireNonNullElse(plugin.fetchPlayer(playerUid), new ImmutablePair<>((String) null,null)).getLeft();
        } catch (IllegalArgumentException | StringIndexOutOfBoundsException e) {
            Pair<String, UUID> data = plugin.fetchPlayer(nameOrUUID);
            playerUid = data == null ? null : data.getRight();
            playerName = data == null ? nameOrUUID : data.getLeft();
        }
        Pair<String, UUID> data = new ImmutablePair<>(playerName, playerUid);
        if (plugin.isWhitelisted(data)) {
            plugin.removeWhitelist(data);
            sender.sendMessage("Un-Whitelisted " + playerName);
        } else {
            plugin.addWhiteList(data);
            sender.sendMessage("Whitelisted " + playerName);
        }

        return false;
    }
}
