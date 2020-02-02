package me.nahu.fairies.manager.tasks;

import me.nahu.fairies.manager.ChatManager;
import me.nahu.fairies.manager.PlayerManager;
import me.nahu.fairies.manager.player.FakePlayer;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

import static me.nahu.fairies.utils.Utilities.RANDOM;

public class ChatTask extends BukkitRunnable {
    private ChatManager chatManager;
    private PlayerManager playerManager;

    public ChatTask(ChatManager chatManager, PlayerManager playerManager) {
        this.chatManager = chatManager;
        this.playerManager = playerManager;
    }

    @Override
    public void run() {
        if (Bukkit.getOnlinePlayers().size() < 1 || playerManager.getPlayerAmount() < 1)
            return;

        FakePlayer fakePlayer = getRandomPlayer();
        String message = chatManager.getRandomMessage();

        fakePlayer.getAsPlayer().chat(message);
    }

    @SuppressWarnings("UnstableApiUsage")
    private FakePlayer getRandomPlayer() {
        List<FakePlayer> spoofedPlayers = new ArrayList<>(playerManager.getPlayerCache().asMap().values());
        return spoofedPlayers.get(RANDOM.nextInt(spoofedPlayers.size()));
    }
}
