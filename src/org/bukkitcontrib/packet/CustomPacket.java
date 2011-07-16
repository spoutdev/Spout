package org.bukkitcontrib.packet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import org.bukkitcontrib.ContribNetServerHandler;
import net.minecraft.server.NetHandler;
import net.minecraft.server.Packet;

public class CustomPacket extends Packet{
	BukkitContribPacket packet;

	public CustomPacket() {
		
	}
	
	public CustomPacket(BukkitContribPacket packet) {
		this.packet = packet;
	}

	@Override
	public int a() {
		return packet.getNumBytes();
	}

	@Override
	public void a(DataInputStream input) throws IOException {
		int packetId = -1;
		packetId = input.readInt();
		input.readInt(); //packet size
		if (packetId > -1) {
			try {
				this.packet = PacketType.getPacketFromId(packetId).getPacketClass().newInstance();
				packet.readData(input);
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void a(DataOutputStream output) throws IOException {
		output.writeInt(packet.getPacketType().getId());
		output.writeInt(a());
		packet.writeData(output);
	}

	@Override
	public void a(NetHandler netHandler) {
		if (netHandler.getClass().hashCode() == ContribNetServerHandler.class.hashCode()) {
			ContribNetServerHandler handler = (ContribNetServerHandler)netHandler;
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
