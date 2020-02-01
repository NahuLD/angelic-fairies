package me.nahu.fairies.helpers.connection;

import me.nahu.fairies.utils.Utilities;
import net.minecraft.server.v1_8_R3.EnumProtocolDirection;
import net.minecraft.server.v1_8_R3.NetworkManager;

import java.lang.reflect.Field;

public class NPCNetworkManager extends NetworkManager {

    public NPCNetworkManager() {
        super(EnumProtocolDirection.CLIENTBOUND); //MCP = isClientSide ---- SRG=field_150747_h
        Field channel = Utilities.makeField(NetworkManager.class, "channel"); //MCP = channel ---- SRG=field_150746_k
        Field address = Utilities.makeField(NetworkManager.class, "l"); //MCP = address ---- SRG=field_77527_e

        Utilities.setField(channel, this, new NullChannel());
        Utilities.setField(address, this, new NullSocketAddress());
    }
}