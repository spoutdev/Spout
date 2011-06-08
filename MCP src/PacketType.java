package net.minecraft.src;
//BukkitContrib
import java.util.HashMap;

public enum PacketType {
	PacketKeyPress(0, PacketKeyPress.class),
	PacketAirTime(1, PacketAirTime.class),
	PacketSkinURL(2, PacketSkinURL.class),
	PacketEntityTitle(3, PacketEntityTitle.class),
	PacketPluginReload(4, PacketPluginReload.class),
	
	;
	
	private final int id;
	private final Class<? extends BukkitContribPacket> packetClass;
	private static final HashMap<Integer, PacketType> lookupId = new HashMap<Integer, PacketType>();
	PacketType(final int type, final Class<? extends BukkitContribPacket> packetClass) {
		this.id = type;
		this.packetClass = packetClass;
	}
	
	public int getId() {
		return id;
	}
	
	public Class<? extends BukkitContribPacket> getPacketClass() {
		return packetClass;
	}
	
	public static PacketType getPacketFromId(int id) {
		return lookupId.get(id);
	}
	
	
	static {
		for (PacketType packet : values()) {
			lookupId.put(packet.getId(), packet);
		}
	}
}
