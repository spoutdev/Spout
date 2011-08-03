package org.getspout.spout.packet;

import org.getspout.spout.packet.listener.PacketListeners;
import org.getspout.spout.packet.standard.MCCraftPacket;
import org.getspout.spoutapi.packet.PacketManager;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;

public class SimplePacketManager implements PacketManager {

	public void addListener(int packetId, PacketListener listener) {
		PacketListeners.addListener(packetId, listener);	
	}

	public void addListenerUncompressedChunk(PacketListener listener) {
		PacketListeners.addListenerUncompressedChunk(listener);
	}

	public MCPacket getInstance(int packetId) {
		return MCCraftPacket.newInstance(packetId);
	}

	public boolean removeListener(int packetId, PacketListener listener) {
		return PacketListeners.removeListener(packetId, listener);
	}

	public boolean removeListenerUncompressedChunk(PacketListener listener) {
		return PacketListeners.removeListenerUncompressedChunk(listener);
	}
	
	public void clearAllListeners() {
		PacketListeners.clearAllListeners();
	}

}
