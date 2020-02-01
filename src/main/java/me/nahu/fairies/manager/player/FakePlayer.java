package me.nahu.fairies.manager.player;

import me.nahu.fairies.helpers.EntityNPC;
import me.nahu.fairies.helpers.ProtocolHelper;
import net.minecraft.server.v1_8_R3.PacketPlayOutPlayerInfo;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.UUID;

public class FakePlayer {
    private static final World DEFAULT_WORLD = Bukkit.getWorlds().get(0);
    public static final Location DEFAULT_LOCATION = new Location(DEFAULT_WORLD, 0, 0, 0);

    private EntityNPC entityPlayer;

    private UUID uniqueId;
    private String name;

    public FakePlayer(UUID uniqueId, String name, int ping) {
        entityPlayer = new EntityNPC(uniqueId, name, ping, DEFAULT_LOCATION);
        this.uniqueId = uniqueId;
        this.name = name;
    }

    public void send(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER,
                entityPlayer);
        ProtocolHelper.getPlayer(player).playerConnection.sendPacket(packet);
    }

    public void send() {
        Bukkit.getOnlinePlayers().forEach(this::send);
    }

    public void remove(Player player) {
        PacketPlayOutPlayerInfo packet = new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER,
                entityPlayer);
        ProtocolHelper.getPlayer(player).playerConnection.sendPacket(packet);
        entityPlayer.removeFromPlayerList();
    }

    public void remove() {
        Bukkit.getOnlinePlayers().forEach(this::remove);
    }

    public String getName() {
        return name;
    }

    public UUID getUniqueId() {
        return uniqueId;
    }

    public EntityNPC getEntityPlayer() {
        return entityPlayer;
    }
}
