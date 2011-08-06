package org.getspout.spout.chunkcache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.EntityPlayer;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.getspout.spout.packet.CustomPacket;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spout.util.ChunkHash;
import org.getspout.spoutapi.packet.PacketCacheHashUpdate;

public class ChunkCache {

	private final static int FULL_CHUNK_SIZE = 81920;
	private final static int CACHED_SIZE = FULL_CHUNK_SIZE + 40*8;

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
			if(spc.getVersion() < 101) { 
				// TODO: fix version, probably 102
				return uncompressedData;
			}
		}

		processHashUpdates();

		LinkedList<HashUpdate> pending = pendingHashUpdates.get(id);
		if (pending != null) {
			NetServerHandler nsh = player.netServerHandler;
			while(!pending.isEmpty()) {
				HashUpdate update = pending.removeFirst();
				if(update.hashes.length != 0) {
					Packet p = new CustomPacket(new PacketCacheHashUpdate(update.add, update.hashes));
					p.k = true;
					nsh.sendPacket(p);
				}
			}
		}

		System.arraycopy(uncompressedData, 0, cachedData, 0, uncompressedData.length);

		HashSet<Long> playerHashes = activeHashes.get(id);
		if(playerHashes == null) {
			playerHashes = new HashSet<Long>();
			activeHashes.put(id, playerHashes);
		}

		int cacheHit = 0;

		for(int i = 0; i < 40; i++) {
			PartitionChunk.copyFromChunkData(cachedData, i, partition);
			long hash = ChunkHash.hash(partition);
			PartitionChunk.setHash(cachedData, i, hash);

			if(!playerHashes.add(hash)) {
				PartitionChunk.copyToChunkData(cachedData, i, null);
				cacheHit++;
			} else {
				PartitionChunk.setHash(cachedData, i, 0);
			}
		}

		return cachedData;

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
