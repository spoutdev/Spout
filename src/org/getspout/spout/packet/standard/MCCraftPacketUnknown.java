package org.getspout.spout.packet.standard;

import org.getspout.spoutapi.packet.standard.MCPacketUnknown;

public class MCCraftPacketUnknown extends MCCraftPacket implements MCPacketUnknown {

	public Object getRawPacket() {
		return packet;
	}
	
}
