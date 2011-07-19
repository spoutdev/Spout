package org.bukkitcontrib.packet.listener;

import net.minecraft.server.Packet;

public class PacketListener {
	
    public boolean outGoingPacket(Packet packet, int packetId) {
        System.out.println("outGoingPacket not overriden, error.");
        //Error happened, so were sending the packet anyway.
        return true;
    }

}
