package org.getspout.spout.packet.standard;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import net.minecraft.server.Packet;

import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spoutapi.packet.standard.MCPacket51MapChunk;
import org.getspout.spoutapi.packet.standard.MCPacket51MapChunkUncompressed;

public class MCCraftPacket implements MCPacket {
	
	Packet packet;
	int packetId;

	public int getId() {
		return packetId;
	}
	
	public void setPacket(Packet packet, int packetId) {
		this.packet = packet;
		this.packetId = packetId;
	}
	
	private static Class<?>[] packets = new Class<?>[257];
	
	static {
		
		packets[0] = MCCraftPacket0KeepAlive.class;
		packets[3] = MCCraftPacket3Chat.class;
		packets[51] = MCCraftPacket51MapChunk.class;
		packets[256] = MCPacket51MapChunkUncompressed.class;
		
	}
	
	private static final Object[] blank = new Class[0];
	
	public static MCCraftPacket newInstance(Packet packet) {
		return newInstance(packet.b(), packet);
	}
	
	public static MCCraftPacket newInstance(int packetId, Packet packet) {
		try {
			@SuppressWarnings("unchecked")
			Class<? extends MCCraftPacket> mcp = (Class<? extends MCCraftPacket>)packets[packetId];
			if(mcp == null) {
				return null;
			}
			Constructor<? extends MCCraftPacket> c = mcp.getConstructor(new Class[] {});
			MCCraftPacket r = c.newInstance(blank);
			r.setPacket(packet, packetId);
			return r;
		} catch (SecurityException e) {
			throw new RuntimeException(e);
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException(e);
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException(e);
		}
	}
	
}
