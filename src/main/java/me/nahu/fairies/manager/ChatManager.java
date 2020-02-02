package me.nahu.fairies.manager;

import me.nahu.fairies.manager.tasks.ChatTask;
import me.nahu.fairies.utils.Utilities;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.List;

public class ChatManager {
    private List<String> messages;
    private int cooldown;

    private BukkitTask chatTask;

    private PlayerManager playerManager;

    public ChatManager(FileConfiguration configuration, PlayerManager playerManager) {
        messages = configuration.getStringList("messages");
        cooldown = configuration.getInt("interval");
        this.playerManager = playerManager;
    }

    public void startTask(Plugin plugin) {
        chatTask = new ChatTask(this, playerManager).runTaskTimer(plugin, 20, cooldown);
    }

    public void stopTask() {
        chatTask.cancel();
    }

    public String getRandomMessage() {
        return messages.get(Utilities.RANDOM.nextInt(messages.size()));
    }

}
