package org.getspout.spout.packet.listener;

import net.minecraft.server.Packet20NamedEntitySpawn;
import net.minecraft.server.Packet21PickupSpawn;
import net.minecraft.server.Packet23VehicleSpawn;
import net.minecraft.server.Packet24MobSpawn;
import net.minecraft.server.Packet25EntityPainting;
import net.minecraft.server.Packet30Entity;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.getspout.spout.Spout;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.packet.PacketUniqueId;
import org.getspout.spoutapi.packet.listener.PacketListener;
import org.getspout.spoutapi.packet.standard.MCPacket;
import org.getspout.spoutapi.player.SpoutPlayer;

public class EntitySpawnListener implements PacketListener{

	@SuppressWarnings("deprecation")
	@Override
	public boolean checkPacket(Player p, MCPacket packet) {
		if (p != null && p.getClass().equals(SpoutCraftPlayer.class)) {
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
					//TODO there must be a better way to do this...
					UpdateUniqueId update = new UpdateUniqueId(player, entityId);
					Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Spout.getInstance(), update);
				}
			}
		}
		return true;
	}
	
	private class UpdateUniqueId implements Runnable {
		private SpoutPlayer player;
		private int entityId;
		
		private UpdateUniqueId(SpoutPlayer player, int id) {
			this.player = player;
			this.entityId = id;
		}

		@Override
		public void run() {
			Entity e = SpoutManager.getEntityFromId(entityId);
			if (e != null) {
				player.sendPacket(new PacketUniqueId(e.getUniqueId(), entityId));
			}
		}
		
	}

}
