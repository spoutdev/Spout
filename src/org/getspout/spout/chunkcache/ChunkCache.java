package org.getspout.spout.chunkcache;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.minecraft.server.EntityPlayer;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spout.util.ChunkHash;

public class ChunkCache {
	
	private final static int FULL_CHUNK_SIZE = 81920;
	private final static int CACHED_SIZE = FULL_CHUNK_SIZE + 40*8;
	
	private static HashMap<Integer,HashSet<Long>> activeHashes = new HashMap<Integer,HashSet<Long>>();
	private static ConcurrentLinkedQueue<Integer> quittingPlayers = new ConcurrentLinkedQueue<Integer>();
	
	private static byte[] cachedData = new byte[CACHED_SIZE];
	private static byte[] partition = new byte[2048];

	public static byte[] cacheChunk(EntityPlayer[] players, byte[] uncompressedData) {
		
		while(!quittingPlayers.isEmpty()) {
			Integer id = quittingPlayers.poll();
			if(id != null) {
				System.out.println("Removing " + id);
				activeHashes.remove(id);
			}
		}
		
		if(uncompressedData.length != FULL_CHUNK_SIZE || players.length != 1) {
			return uncompressedData;
		}
		
		EntityPlayer player = players[0];
		
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
		
		System.arraycopy(uncompressedData, 0, cachedData, 0, uncompressedData.length);
		
		int id = player.id;
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
	
	public static void playerQuit(int id) {
		quittingPlayers.add(id);
	}
	
}
