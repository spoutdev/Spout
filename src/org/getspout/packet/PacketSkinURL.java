package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketSkinURL implements BukkitContribPacket{
	
	public PacketSkinURL() {
	}
	
	public PacketSkinURL(int id, String skinURL, String cloakURL) {
		this.entityId = id;
		this.skinURL = skinURL;
		this.cloakURL = cloakURL;
	}
	
	public PacketSkinURL(int id, String skinURL) {
		this.entityId = id;
		this.skinURL = skinURL;
		this.cloakURL = "none";
	}
	
	public PacketSkinURL(String cloakURL, int id) {
		this.entityId = id;
		this.skinURL = "none";
		this.cloakURL = cloakURL;
	}
	public int entityId;
	public String skinURL;
	public String cloakURL;

	@Override
	public int getNumBytes() {
		return 4 + PacketUtil.getNumBytes(skinURL) + PacketUtil.getNumBytes(cloakURL);
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		entityId = input.readInt();
		skinURL = PacketUtil.readString(input, 256);
		cloakURL = PacketUtil.readString(input, 256);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(entityId);
		PacketUtil.writeString(output, skinURL);
		PacketUtil.writeString(output, cloakURL);
	}

	@Override
	public void run(int PlayerId) {
		
	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketSkinURL;
	}
}
