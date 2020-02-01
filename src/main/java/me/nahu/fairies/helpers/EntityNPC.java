package me.nahu.fairies.helpers;

import me.nahu.fairies.helpers.connection.NPCConnection;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftServer;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.UUID;

public class EntityNPC extends EntityPlayer {
    private static final PlayerList PLAYER_LIST = ((CraftServer) Bukkit.getServer()).getHandle();

    private WorldServer worldServer;
    private Location location;

    public EntityNPC(UUID uniqueId, String name, int ping, Location location) {
        super(ProtocolHelper.getServer(),
              ProtocolHelper.getWorld(location.getWorld()),
              ProtocolHelper.getGameProfile(uniqueId, name),
              new PlayerInteractManager(ProtocolHelper.getWorld(location.getWorld())));
        playerInteractManager.b(WorldSettings.EnumGamemode.SURVIVAL);
        this.playerConnection = new NPCConnection(this);

        this.ping = ping;
        this.location = location;

        worldServer = ProtocolHelper.getWorld(location.getWorld());
        worldServer.players.remove(this);
    }

    @SuppressWarnings("unchecked")
    private void addToPlayerList(WorldServer worldServer, UUID uniqueId) {
        PLAYER_LIST.players.add(this);
        PLAYER_LIST.a(this, worldServer);
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
    public void removeFromPlayerList(UUID uniqueId) {
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
