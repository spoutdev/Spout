package org.getspout.spout.packet.standard;

import net.minecraft.server.Packet0KeepAlive;

import org.getspout.spoutapi.packet.standard.MCPacket0KeepAlive;

public class MCCraftPacket0KeepAlive extends MCCraftPacket implements MCPacket0KeepAlive {

	public Packet0KeepAlive getPacket() {
		return (Packet0KeepAlive) packet;
	}
	
}
