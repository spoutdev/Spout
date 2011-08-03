package org.getspout.spout.packet.standard;

import net.minecraft.server.Packet3Chat;

import org.getspout.spoutapi.packet.standard.MCPacket3Chat;

public class MCCraftPacket3Chat extends MCCraftPacket implements MCPacket3Chat {

	public Packet3Chat getPacket() {
		return (Packet3Chat)packet;
	}
	
	public String getMessage() {
		return getPacket().message;
	}

	public void setMessage(String message) {
		getPacket().message = message;
	}

}
