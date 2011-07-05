package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketItemName implements BukkitContribPacket{
	private int id;
	private byte data;
	private String name;
	public PacketItemName() {
		
	}
	
	public PacketItemName(int id, byte data, String name) {
		this.id = id;
		this.data = data;
		this.name = name;
	}

	@Override
	public int getNumBytes() {
		return 5 + name.length();
	}

	@Override
	public void readData(DataInputStream input) throws IOException {
		id = input.readInt();
		data = input.readByte();
		name = PacketUtil.readString(input);
	}

	@Override
	public void writeData(DataOutputStream output) throws IOException {
		output.writeInt(id);
		output.writeByte(data);
		PacketUtil.writeString(output, name);
	}

	@Override
	public void run(int PlayerId) {

	}

	@Override
	public PacketType getPacketType() {
		return PacketType.PacketItemName;
	}

}
