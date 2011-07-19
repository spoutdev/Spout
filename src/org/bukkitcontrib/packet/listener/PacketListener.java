package org.bukkitcontrib.packet.listener;

import net.minecraft.server.Packet;

public interface PacketListener {

    public boolean outGoingPacket(Packet packet, int packetId);

}
