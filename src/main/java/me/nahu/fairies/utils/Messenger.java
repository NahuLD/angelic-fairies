package me.nahu.fairies.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Messenger {
    private FileConfiguration file;
    private String prefix;

    public Messenger(FileConfiguration file) {
        this.file = file;
        this.prefix = file.getString("prefix");
    }

    public Builder get(String path) {
        return new Builder(prefix, file.getString(path));
    }

    public List<String> getColoredList(String path) {
        return file.getStringList(path).stream().map(Messenger::color).collect(Collectors.toList());
    }

    public class Builder {
        private String message;
        private String prefix;
        private boolean usePrefix = true;

        private Builder(String prefix, String message) {
            this.prefix = prefix;
            this.message = message;
        }

        public Builder replace(String replaced, String replacee) {
            message = message.replace(replaced, replacee);
            return this;
        }

        public Builder replace(String replaced, Object replacee) {
            message = message.replace(replaced, String.valueOf(replacee));
            return this;
        }

        public Builder replace(String key, Player player) {
            replace(key, player.getName());
            return this;
        }

        public Builder replace(Player player) {
            replace("%player", player);
            return this;
        }

        public Builder usePrefix(boolean usePrefix) {
            this.usePrefix = usePrefix;
            return this;
        }

        public void send(Collection<? extends Player> players) {
            players.forEach(player -> player.sendMessage(getAsString()));
        }

        public void send(CommandSender... senders) {
            String sent = getAsString();
            for (CommandSender sender : senders)
                sender.sendMessage(sent);
        }

        public void broadcast() {
            Bukkit.broadcastMessage(getAsString());
        }

        public String getAsString() {
            return color((usePrefix) ? prefix + message : message);
        }
    }

    public static String color(String input) {
        return ChatColor.translateAlternateColorCodes('&', input);
    }
}