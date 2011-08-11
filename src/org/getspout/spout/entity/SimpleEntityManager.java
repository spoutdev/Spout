package org.getspout.spout.entity;

import org.bukkit.entity.Entity;
import org.getspout.spoutapi.entity.EntityManager;
import org.getspout.spoutapi.packet.PacketEntitySkin;
import org.getspout.spoutapi.player.SpoutPlayer;

public class SimpleEntityManager implements EntityManager {

	@Override
	public void setTexture(SpoutPlayer player, Entity entity, String texture) {
		PacketEntitySkin packet = new PacketEntitySkin(entity, texture, true);
		player.sendPacket(packet);
	}

	@Override
	public void setAlternateTexture(SpoutPlayer player, Entity entity,
			String texture) {
		PacketEntitySkin packet = new PacketEntitySkin(entity, texture, false);
		player.sendPacket(packet);
	}

	
}
