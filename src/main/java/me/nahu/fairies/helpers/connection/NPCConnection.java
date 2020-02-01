package me.nahu.fairies.helpers.connection;

import me.nahu.fairies.helpers.ProtocolHelper;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PlayerConnection;

public class NPCConnection extends PlayerConnection {

    public NPCConnection(EntityPlayer player) {
        super(ProtocolHelper.getServer(), new NPCNetworkManager(), player);
    }

    @Override
    public void sendPacket(Packet packet) {
        // lets not
    }
}