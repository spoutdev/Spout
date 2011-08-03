package org.getspout.spout.packet.standard;

import net.minecraft.server.Packet51MapChunk;

import org.getspout.spoutapi.packet.standard.MCPacket51MapChunk;

public class MCCraftPacket51MapChunk extends MCCraftPacket implements MCPacket51MapChunk {

	private Packet51MapChunk getPacket() {
		return (Packet51MapChunk)packet;
	}
	
	public int getX() {
		return getPacket().a;
	}

	public int getY() {
		return getPacket().b;
	}

	public int getZ() {
		return getPacket().c;
	}

	public void setX(int x) {
		getPacket().a = x;
	}

	public void setY(int y) {
		getPacket().b = y;
	}

	public void setZ(int z) {
		getPacket().c = z;
	}

	public byte[] getCompressedChunkData() {
		return getPacket().g;
	}

	public int getSizeX() {
		return getPacket().d;
	}

	public int getSizeY() {
		return getPacket().e;
	}

	public int getSizeZ() {
		return getPacket().f;
	}

	public void setSizeX(int x) {
		getPacket().d = x;
	}

	public void setSizeY(int y) {
		getPacket().e = y;
	}

	public void setSizeZ(int z) {
		getPacket().f = z;
	}
	
}
