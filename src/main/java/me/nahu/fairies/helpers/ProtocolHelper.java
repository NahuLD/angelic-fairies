package me.nahu.fairies.helpers;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.authlib.GameProfile;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.WorldServer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.IOException;
import java.net.URL;
import java.util.Optional;
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

    public static Optional<Pair<UUID, String>> getUniqueId(String name) {
        String url = "https://api.mojang.com/users/profiles/minecraft/".concat(name);
        try {
            String UUIDJson = IOUtils.toString(new URL(url));
            if(UUIDJson.isEmpty()) return Optional.of(Pair.of(UUID.randomUUID(), name));

            JSONObject object = (JSONObject) JSONValue.parseWithException(UUIDJson);

            String formattedId = object.get("id").toString().replaceAll(
                    "(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})",
                    "$1-$2-$3-$4-$5");
            String formattedName = object.get("name").toString();
            return Optional.of(Pair.of(UUID.fromString(formattedId), formattedName));
        } catch (IOException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
        }
        return Optional.of(Pair.of(UUID.randomUUID(), name));
    }
}