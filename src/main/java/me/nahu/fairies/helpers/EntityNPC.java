package me.nahu.fairies.helpers;

import me.nahu.fairies.helpers.connection.NPCConnection;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

import static me.nahu.fairies.manager.player.FakePlayer.DEFAULT_LOCATION;

public class EntityNPC extends EntityPlayer {
    private static final MinecraftServer SERVER = ProtocolHelper.getServer();
    private static final PlayerList PLAYER_LIST = ((CraftServer) Bukkit.getServer()).getHandle();
    private static final WorldServer WORLD_SERVER = ProtocolHelper.getWorld(DEFAULT_LOCATION.getWorld());

    private UUID uniqueId;

    public EntityNPC(UUID uniqueId, String name, int ping, Location location) {
        super(SERVER,
              WORLD_SERVER,
              ProtocolHelper.getGameProfile(uniqueId, name),
              new PlayerInteractManager(ProtocolHelper.getWorld(location.getWorld())));
        playerInteractManager.b(WorldSettings.EnumGamemode.SURVIVAL);
        this.playerConnection = new NPCConnection(this);

        this.ping = ping;
        this.uniqueId = uniqueId;

        addToPlayerList();
        WORLD_SERVER.players.remove(this);
    }

    @SuppressWarnings("unchecked")
    private void addToPlayerList() {
        PLAYER_LIST.players.add(this);
        PLAYER_LIST.a(this, WORLD_SERVER);
        try {
            Field field = PlayerList.class.getDeclaredField("j");
            field.setAccessible(true);
            Map<UUID, EntityPlayer> j = (Map) field.get(PLAYER_LIST);
            j.put(uniqueId, this);
            field.set(PLAYER_LIST, j);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public void removeFromPlayerList() {
        try {
            Field field = PlayerList.class.getDeclaredField("j");
            field.setAccessible(true);
            Map<UUID, EntityPlayer> j = (Map) field.get(PLAYER_LIST);
            j.remove(uniqueId);
            field.set(PLAYER_LIST, j);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            playerConnection.disconnect("connection interrupted");
        } catch (Exception ignored) { }
    }
}
