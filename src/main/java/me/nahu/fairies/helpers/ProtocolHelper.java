package me.nahu.fairies.helpers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class ProtocolHelper {
    @SuppressWarnings("UnstableApiUsage")
    private static final LoadingCache<UUID, GameProfile> PROPERTIES_CACHE = CacheBuilder.newBuilder()
            .expireAfterAccess(5, TimeUnit.MINUTES)
            .build(new CacheLoader<UUID, GameProfile>() {
                @Override
                public GameProfile load(@NotNull UUID uniqueId) throws Exception {
                    return MinecraftServer.getServer().aD().fillProfileProperties(
                            new GameProfile(uniqueId, null), true);
                }
            });

    public static MinecraftServer getServer() {
        return ((CraftServer) Bukkit.getServer()).getServer();
    }

    public static WorldServer getWorld(World world) {
        return ((CraftWorld) world).getHandle();
    }

    public static EntityPlayer getPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }

    @SuppressWarnings("UnstableApiUsage")
    public static GameProfile getGameProfile(UUID uniqueId, String name) {
        GameProfile profile = new GameProfile(uniqueId, name);
        GameProfile skinProfile = PROPERTIES_CACHE.getUnchecked(uniqueId);
        profile.getProperties().removeAll("textures");
        profile.getProperties().putAll("textures", skinProfile.getProperties().get("textures"));
        return profile;
    }
}