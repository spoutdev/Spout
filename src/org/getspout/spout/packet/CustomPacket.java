package org.getspout.spout.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

import org.getspout.spout.SpoutNetServerHandler;
import org.getspout.spoutapi.packet.PacketType;
import org.getspout.spoutapi.packet.SpoutPacket;

public class CustomPacket extends Packet{
	public SpoutPacket packet;

	public CustomPacket() {
		
	}
	
	public CustomPacket(SpoutPacket packet) {
		this.packet = packet;
	}

	@Override
	public int a() {
		if(packet == null) {
			return 8;
		} else {
			return packet.getNumBytes() + 8;
		}
	}

	@Override
	public void a(DataInputStream input) throws IOException {
		int packetId = -1;
		packetId = input.readInt();
		int length = input.readInt(); //packet size
		if (packetId > -1) {
				try {
					this.packet = PacketType.getPacketFromId(packetId).getPacketClass().newInstance();
				}
				catch (Exception e) {
					System.out.println("Failed to identify packet id: " + packetId);
					e.printStackTrace();
				}
				try {
					if(this.packet == null) {
						input.skipBytes(length);
						System.out.println("Unknown packet " + packetId + ". Skipping contents.");
						return;
					}
					else {
						packet.readData(input);
						//System.out.println("Reading Packet Data for " +  PacketType.getPacketFromId(packetId));
					}
				}
				catch (IOException e) {
					throw new IOException(e);
				}
				catch (Exception e) {
					e.printStackTrace();
					throw new IllegalStateException("readData() for packetId " + packetId + " threw an exception");
				}
		}
	}

	@Override
	public void a(DataOutputStream output) throws IOException {
		if(packet == null) {
			output.writeInt(-1);
			output.writeInt(0);;
			return;
		}
		//System.out.println("Writing Packet Data for " + packet.getPacketType());
		output.writeInt(packet.getPacketType().getId());
		output.writeInt(a() - 8);
		packet.writeData(output);
	}

	@Override
	public void a(NetHandler netHandler) {
		if (netHandler.getClass().hashCode() == SpoutNetServerHandler.class.hashCode()) {
			SpoutNetServerHandler handler = (SpoutNetServerHandler)netHandler;
			packet.run(handler.getPlayer().getEntityId());
		}
		else {
			//System.out.println("Invalid hash!");
		}
	}
	
	public static void addClassMapping() {
		try {
			Class<?>[] params = {int.class, boolean.class, boolean.class, Class.class};
			Method addClassMapping = Packet.class.getDeclaredMethod("a", params);
			addClassMapping.setAccessible(true);
			addClassMapping.invoke(null, 195, true, true, CustomPacket.class);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static void removeClassMapping() {
		try {
			Field field = Packet.class.getDeclaredField("a");
			field.setAccessible(true);
			Map temp = (Map) field.get(null);
			temp.remove(195);
			field = Packet.class.getDeclaredField("b");
			field.setAccessible(true);
			temp = (Map) field.get(null);
			temp.remove(CustomPacket.class);
		}
		catch (Exception e) {
			
		}
	}

}
