package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public interface BukkitContribPacket {
	
	public int getNumBytes();
	
	public void readData(DataInputStream input) throws IOException;
	
	public void writeData(DataOutputStream output) throws IOException;
	
	public void run(int PlayerId);
	
	public PacketType getPacketType();

}
