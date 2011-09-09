package org.getspout.spout.packet.listener;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet25EntityPainting;
import net.minecraft.server.Packet30Entity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketUniqueId;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EntitySpawnListener implements PacketListener{

	@SuppressWarnings("deprecation")
	@Override
	public boolean checkPacket(Player p, MCPacket packet) {
		if (p != null) {
			SpoutPlayer player = (SpoutPlayer)p;
			if (player.isSpoutCraftEnabled()) {
				Object raw = packet.getPacket();
				int entityId = -1;
				if (raw instanceof Packet30Entity) {
					entityId = ((Packet30Entity)raw).a;
				}
				else if (raw instanceof Packet20NamedEntitySpawn) {
					entityId = ((Packet20NamedEntitySpawn)raw).a;
				}
				else if (raw instanceof Packet21PickupSpawn) {
					entityId = ((Packet21PickupSpawn)raw).a;
				}
				else if (raw instanceof Packet24MobSpawn) {
					entityId = ((Packet24MobSpawn)raw).a;
				}
				else if (raw instanceof Packet23VehicleSpawn) {
					entityId = ((Packet23VehicleSpawn)raw).a;
				}
				else if (raw instanceof Packet25EntityPainting) {
					entityId = ((Packet25EntityPainting)raw).a;
				}
				if (entityId != -1) {
					Entity e = SpoutManager.getEntityFromId(entityId);
					player.sendPacket(new PacketUniqueId(e.getUniqueId(), entityId));
				}
			}
		}
		return true;
	}

}
