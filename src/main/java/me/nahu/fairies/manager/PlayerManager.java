package me.nahu.fairies.manager;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.cache.RemovalNotification;
import me.nahu.fairies.manager.player.FakePlayer;
import me.nahu.fairies.utils.Utilities;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

@SuppressWarnings("UnstableApiUsage")
public class PlayerManager {
    private final LoadingCache<Pair<UUID, String>, FakePlayer> playerCache = CacheBuilder.newBuilder()
            .removalListener(this::onRemoval)
            .build(getCacheLoader());

    private int maxPing, minPing, pingFluctuation;

    public PlayerManager() {

    }

    private FakePlayer addPlayer(String name, UUID uniqueId) {
        return new FakePlayer(uniqueId, name, Utilities.getRandomNumberFromBoundary(maxPing, minPing, pingFluctuation));
    }

    @SuppressWarnings("UnstableApiUsage")
    private void onRemoval(RemovalNotification<Pair<UUID, String>, FakePlayer> removalNotification) {
        FakePlayer player = removalNotification.getValue();
        player.remove();
    }
    
    private CacheLoader<Pair<UUID, String>, FakePlayer> getCacheLoader() {
        return new CacheLoader<Pair<UUID, String>, FakePlayer>() {
            @Override
            public FakePlayer load(@NotNull Pair<UUID, String> player) {
                // TODO send message
                return addPlayer(player.getRight(), player.getLeft());
            }
        };
    }
}
