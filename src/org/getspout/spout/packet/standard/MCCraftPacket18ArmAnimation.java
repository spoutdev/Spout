package org.getspout.spout.packet.standard;

import net.minecraft.server.Packet18ArmAnimation;

import org.getspout.spoutapi.packet.standard.MCPacket18ArmAnimation;

public class MCCraftPacket18ArmAnimation extends MCCraftPacket implements MCPacket18ArmAnimation {

	public Packet18ArmAnimation getPacket() {
		return (Packet18ArmAnimation)packet;
	}
	
	public int getAnimate() {
		return getPacket().b;
	}

	public int getEntityId() {
		return getPacket().a;
	}

	public void setAnimate(int animate) {
		getPacket().b = animate;
	}

	public void setEntityId(int id) {
		getPacket().a = id;
	}

}
