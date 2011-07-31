package org.getspout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class PacketNotification extends PacketBukkitContribAlert{
	protected int time;
	protected short data;
	public PacketNotification() {
		
	}
	
	public PacketNotification(String title, String message, int itemId, short data, int time) {
		super(title, message, itemId);
		this.time = time;
		this.data = data;
	}
	
	@Override
	public int getNumBytes() {
		return super.getNumBytes() + 6;
	}
	
	@Override
	public void readData(DataInputStream input) throws IOException {
		super.readData(input);
		this.data = input.readShort();
		this.time = input.readInt();
	}
	
	@Override
	public void writeData(DataOutputStream output) throws IOException {
		super.writeData(output);
		output.writeShort(data);
		output.writeInt(time);
	}
	
	@Override
	public PacketType getPacketType() {
		return PacketType.PacketNotification;
	}

}
