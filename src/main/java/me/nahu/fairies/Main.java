package me.nahu.fairies;

import co.aikar.commands.BukkitCommandManager;
import me.nahu.fairies.command.PlayerCommand;
import me.nahu.fairies.listener.PlayerListener;
import me.nahu.fairies.manager.ChatManager;
import me.nahu.fairies.manager.PlayerManager;
import me.nahu.fairies.manager.player.FakePlayer;
import me.nahu.fairies.utils.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.stream.Stream;

public class Main extends JavaPlugin {
    private Messenger messenger;

    private PlayerManager playerManager;
    private ChatManager chatManager;

    @Override
    public void onEnable() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.enableUnstableAPI("help");
        commandManager.usePerIssuerLocale(false, false);

        saveResource("config.yml", false);
        saveResource("chat.yml", false);
        Stream.of("en_US") // one language only, hah
                .map("lang/"::concat)
                .map(fileName -> fileName.concat(".yml"))
                .forEach(file -> saveResource(file, false));

        messenger = loadMessenger(getConfig());
        playerManager = new PlayerManager(messenger, getConfig());

        chatManager = new ChatManager(YamlConfiguration.loadConfiguration(new File(getDataFolder(), "chat.yml")), playerManager);

        commandManager.registerDependency(Messenger.class, messenger);
        commandManager.registerDependency(PlayerManager.class, playerManager);

        commandManager.registerCommand(new PlayerCommand());

        // events
        Bukkit.getPluginManager().registerEvents(new PlayerListener(playerManager), this);

        chatManager.startTask(this);
    }

    private Messenger loadMessenger(FileConfiguration configuration) {
        String language = configuration.getString("language");
        File languageFile = new File(getDataFolder().getPath() + "/lang", language.concat(".yml"));
        return new Messenger(YamlConfiguration.loadConfiguration(languageFile));
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onDisable() {
        chatManager.stopTask();
        playerManager.getPlayerCache().asMap().values().forEach(FakePlayer::remove);
    }
}
