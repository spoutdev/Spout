/*
 * This file is part of Spout (http://wiki.getspout.org/).
 * 
 * Spout is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Spout is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.getspout.spout.chunkcache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.Packet;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.getspout.spout.MapChunkThread;
import org.getspout.spout.config.ConfigReader;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spout.util.ChunkHash;
import org.getspout.spoutapi.packet.PacketCacheHashUpdate;

public class ChunkCache {

	private final static int FULL_CHUNK_SIZE = 81920;
	private final static int CACHED_SIZE = FULL_CHUNK_SIZE + 40*8 + 8;

	private static HashMap<Integer,HashSet<Long>> activeHashes = new HashMap<Integer,HashSet<Long>>();
	private static ConcurrentLinkedQueue<Integer> quittingPlayers = new ConcurrentLinkedQueue<Integer>();
	private static ConcurrentLinkedQueue<HashUpdate> hashUpdateSync = new ConcurrentLinkedQueue<HashUpdate>();
	private static HashMap<Integer,LinkedList<HashUpdate>> pendingHashUpdates = new HashMap<Integer,LinkedList<HashUpdate>>();

	private static byte[] cachedData = new byte[CACHED_SIZE];
	private static byte[] partition = new byte[2048];

	public static byte[] cacheChunk(EntityPlayer[] players, byte[] uncompressedData) {

		while(!quittingPlayers.isEmpty()) {
			Integer id = quittingPlayers.poll();
			if(id != null) {
				activeHashes.remove(id);
				pendingHashUpdates.remove(id);
			}
		}

		if(!ConfigReader.isChunkDataCache()) {
			return uncompressedData;
		}
		
		if(uncompressedData.length != FULL_CHUNK_SIZE || players.length != 1) {
			return uncompressedData;
		}

		EntityPlayer player = players[0];

		int id = player.id;

		CraftPlayer cp = player.netServerHandler.getPlayer();
		if(!(cp instanceof SpoutCraftPlayer)) {
			return uncompressedData;
		} else {
			SpoutCraftPlayer spc = (SpoutCraftPlayer)cp;
			if(!spc.isSpoutCraftEnabled()) { 
				return uncompressedData;
			}
		}

		processHashUpdates();

		LinkedList<HashUpdate> pending = pendingHashUpdates.get(id);
		if (pending != null) {
			while(!pending.isEmpty()) {
				HashUpdate update = pending.removeFirst();
				if(update.hashes.length != 0) {
					Packet p = new CustomPacket(new PacketCacheHashUpdate(update.add, update.hashes));
					p.l = true;
					MapChunkThread.sendPacketSkipQueue(player, p);
				}
			}
		}

		System.arraycopy(uncompressedData, 0, cachedData, 0, uncompressedData.length);

		HashSet<Long> playerHashes = activeHashes.get(id);
		if(playerHashes == null) {
			playerHashes = new HashSet<Long>();
			activeHashes.put(id, playerHashes);
		}

		long CRC = ChunkHash.hash(uncompressedData);
		PartitionChunk.setHash(cachedData, 40, CRC);
		
		for(int i = 0; i < 40; i++) {
			PartitionChunk.copyFromChunkData(cachedData, i, partition);
			long hash = ChunkHash.hash(partition);
			PartitionChunk.setHash(cachedData, i, hash);
			
			if(!playerHashes.add(hash)) {
				PartitionChunk.copyToChunkData(cachedData, i, null);
			} else {
				PartitionChunk.setHash(cachedData, i, 0);
			}
		}
		
		byte[] newData = new byte[cachedData.length];
		System.arraycopy(cachedData, 0, newData, 0, cachedData.length);
		
		return newData;

	}

	public static boolean handleCustomPacket(EntityPlayer[] players, CustomPacket packet) {

		if(packet.packet instanceof PacketCacheHashUpdate) {
			EntityPlayer player = players[0];
			int id = player.id;

			HashSet<Long> hashes = activeHashes.get(id);

			PacketCacheHashUpdate updatePacket = (PacketCacheHashUpdate)packet.packet;
			
			if(updatePacket.reset) {
				if(hashes != null) {
					hashes.clear();
				}
				return true;
			}
			
			if(!updatePacket.add) {
				if(hashes != null) {
					for (long hash : updatePacket.hashes) {
						hashes.remove(hash);
					}
				}
				return true;
			} else {
				if(hashes == null) {
					hashes = new HashSet<Long>();
					activeHashes.put(id, hashes);
				}
				for (long hash : updatePacket.hashes) {
					hashes.add(hash);
				}
				return false;
			}
		} else {
			return true;
		}
	}

	public static void playerQuit(int id) {
		quittingPlayers.add(id);
	}

	public static void addToHashUpdateQueue(int id, boolean add, long[] hashes) {

		HashUpdate update = new HashUpdate(id, add, hashes);
		hashUpdateSync.add(update);

	}

	private static void processHashUpdates() {

		while(!hashUpdateSync.isEmpty()) {
			HashUpdate update = hashUpdateSync.poll();
			if(update != null) {
				LinkedList<HashUpdate> pending = pendingHashUpdates.get(update.id);
				if(pending == null) {
					pending = new LinkedList<HashUpdate>();
					pendingHashUpdates.put(update.id, pending);
				}
				pending.add(update);
			}
		}

	}

	private static class HashUpdate {

		int id;
		boolean add;
		long[] hashes;

		HashUpdate(int id, boolean add, long[] hashes) {
			this.id = id;
			this.add = add;
			this.hashes = hashes;
		}

	}

}
