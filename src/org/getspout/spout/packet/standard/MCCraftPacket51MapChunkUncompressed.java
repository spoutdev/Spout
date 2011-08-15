package org.getspout.spout.packet.standard;

import org.getspout.spoutapi.packet.standard.MCPacket51MapChunkUncompressed;

public class MCCraftPacket51MapChunkUncompressed extends MCCraftPacket51MapChunk implements MCPacket51MapChunkUncompressed {

	public byte[] getCompressedChunkData() {
		throw new IllegalStateException("MCCraftPacket51MapChunkUncompressed packets don't have compressed chunk data");
	}
	
	public byte[] getUncompressedChunkData() {
		
		byte[] raw = super.getPacket().rawData; 
		if (raw == null) {
			return super.getCompressedChunkData();
		} else {
			return raw;
		}
	}

}
