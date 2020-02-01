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

    private Messenger messenger;
    private int maxPing, minPing, pingFluctuation;

    public PlayerManager(Messenger messenger, FileConfiguration configuration) {
        this.messenger = messenger;

        maxPing = configuration.getInt("ping.max");
        minPing = configuration.getInt("ping.min");
        pingFluctuation = configuration.getInt("ping.fluctuation");
    }

    public Optional<FakePlayer> getPlayer(String name) {
        return playerCache.asMap().values().stream().filter(spoofedPlayer -> spoofedPlayer.getName().equals(name)).findFirst();
    }

    public FakePlayer addPlayer(String name) throws IllegalArgumentException {
        UUID uniqueId = ProtocolHelper.getUniqueId(name).orElseThrow(IllegalArgumentException::new);
        playerIds.put(name, uniqueId);
        try {
            FakePlayer fakePlayer = playerCache.get(uniqueId);
            fakePlayer.send();
            return fakePlayer;
        } catch (ExecutionException ignore) { }
        return null;
    }

    public void removePlayer(FakePlayer fakePlayer) {
        playerIds.remove(fakePlayer.getName());
        playerCache.invalidate(fakePlayer.getUniqueId());
    }

    private FakePlayer getPlayerFromUniqueId(UUID uniqueId) {
        return new FakePlayer(
                uniqueId,
                playerIds.inverse().get(uniqueId),
                Utilities.getRandomNumberFromBoundary(maxPing, minPing, pingFluctuation)
        );
    }

    public LoadingCache<UUID, FakePlayer> getPlayerCache() {
        return playerCache;
    }

    @SuppressWarnings({"UnstableApiUsage", "ConstantConditions"})
    private void onRemoval(RemovalNotification<UUID, FakePlayer> removalNotification) {
        FakePlayer player = removalNotification.getValue();
        player.remove();
    }
    
    private CacheLoader<UUID, FakePlayer> getCacheLoader() {
        return new CacheLoader<UUID, FakePlayer>() {
            @Override
            public FakePlayer load(@NotNull UUID uniqueId) {
                FakePlayer fakePlayer = getPlayerFromUniqueId(uniqueId);
                messenger.get("messages.join").replace("%player", fakePlayer.getName()).broadcast();
                return fakePlayer;
            }
        };
    }
}
