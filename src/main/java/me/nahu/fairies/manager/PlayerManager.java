package me.nahu.fairies.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.nahu.fairies.helpers.ProtocolHelper;
import me.nahu.fairies.manager.player.FakePlayer;
import me.nahu.fairies.utils.Messenger;
import me.nahu.fairies.utils.Utilities;
import net.luckperms.api.LuckPerms;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("UnstableApiUsage")
public class PlayerManager {
    private final LoadingCache<UUID, FakePlayer> playerCache = CacheBuilder.newBuilder()
            .removalListener(this::onRemoval)
            .build(getCacheLoader());
    private final BiMap<String, UUID> playerIds = HashBiMap.create();

    private LuckPerms luckPerms;
    private String groupName;

    private Messenger messenger;
    private int maxPing, minPing, pingFluctuation;

    public PlayerManager(Messenger messenger, LuckPerms luckPerms, FileConfiguration configuration) {
        this.messenger = messenger;

        this.luckPerms = luckPerms;
        groupName = configuration.getString("permissions.group");

        maxPing = configuration.getInt("ping.max");
        minPing = configuration.getInt("ping.min");
        pingFluctuation = configuration.getInt("ping.fluctuation");
    }

    public Optional<FakePlayer> getPlayer(String name) {
        return playerCache.asMap().values().stream().filter(spoofedPlayer -> spoofedPlayer.getName().equals(name)).findFirst();
    }

    public FakePlayer addPlayer(String name) throws IllegalArgumentException {
        Pair<UUID, String> player = ProtocolHelper.getUniqueId(name).orElseThrow(IllegalArgumentException::new);
        playerIds.put(player.getRight(), player.getLeft());
        try {
            FakePlayer fakePlayer = playerCache.get(player.getLeft());
            fakePlayer.send();
            return fakePlayer;
        } catch (ExecutionException ignore) { }
        return null;
    }

    public void removePlayer(FakePlayer fakePlayer) {
        playerCache.invalidate(fakePlayer.getUniqueId());
    }

    private FakePlayer getPlayerFromUniqueId(UUID uniqueId) {
        return new FakePlayer(
                uniqueId,
                playerIds.inverse().get(uniqueId),
                Utilities.getRandomNumberFromBoundary(maxPing, minPing, pingFluctuation)
        );
    }

    public int getPlayerAmount() {
        return playerCache.asMap().size();
    }

    public LoadingCache<UUID, FakePlayer> getPlayerCache() {
        return playerCache;
    }

    @SuppressWarnings({"UnstableApiUsage", "ConstantConditions"})
    private void onRemoval(RemovalNotification<UUID, FakePlayer> removalNotification) {
        FakePlayer player = removalNotification.getValue();
        playerIds.remove(player.getName());
        player.remove();
    }
    
    private CacheLoader<UUID, FakePlayer> getCacheLoader() {
        return new CacheLoader<UUID, FakePlayer>() {
            @Override
            public FakePlayer load(@NotNull UUID uniqueId) {
                FakePlayer fakePlayer = getPlayerFromUniqueId(uniqueId);

                Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                        "lp user " + fakePlayer.getUniqueId().toString() + " parent add " + groupName);
                messenger.get("messages.join")
                        .replace("%player", fakePlayer.getName())
                        .usePrefix(false)
                        .broadcast();
                return fakePlayer;
            }
        };
    }
}
