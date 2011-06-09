package net.minecraft.src;
//BukkitContrib

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class CustomPacket extends Packet{
	BukkitContribPacket packet;

	public CustomPacket() {
		
	}
	
	public CustomPacket(BukkitContribPacket packet) {
		this.packet = packet;
	}

	@Override
	public int getPacketSize() {
		return packet.getNumBytes();
	}

	@Override
	public void readPacketData(DataInputStream input) throws IOException {
		int packetId = -1;
		packetId = input.readInt();
		if (packetId > -1) {
			try {
				this.packet = PacketType.getPacketFromId(packetId).getPacketClass().newInstance();
				packet.readData(input);
				//System.out.println("Reading Packet Data for " +  PacketType.getPacketFromId(packetId));
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void writePacketData(DataOutputStream output) throws IOException {
		//System.out.println("Writing Packet Data for " + packet.getPacketType());
		output.writeInt(packet.getPacketType().getId());
		packet.writeData(output);
	}

	@Override
	public void processPacket(NetHandler netHandler) {
		NetClientHandler handler = (NetClientHandler)netHandler;
		packet.run(BukkitContrib.getGameInstance().thePlayer.entityId);
	}
	
	public static void addClassMapping() {
        addIdClassMapping(195, true, true, CustomPacket.class);
	}
}
