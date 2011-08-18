package org.getspout.spout.chunkcache;

import net.minecraft.server.Packet;
import net.minecraft.server.Packet51MapChunk;
import net.minecraft.server.World;

import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.getspout.spout.SpoutNetServerHandler;
import org.getspout.spout.player.SpoutCraftPlayer;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.chunkcache.CacheManager;

public class SimpleCacheManager implements CacheManager {

	@Override
	public void handle(int id, boolean add, long[] hashes) {
		ChunkCache.addToHashUpdateQueue(id, add, hashes);
	}
	
	@Override
	public void refreshChunkRequest(int id, int cx, int cz) {
		SpoutCraftPlayer player = (SpoutCraftPlayer)SpoutManager.getPlayerFromId(id);
		World world = player.getHandle().world;
		Packet packet51 = new Packet51MapChunk(cx << 4, 0, cz << 4, 16, 128, 16, world);
		player.getNetServerHandler().sendPacket(packet51);
		SpoutNetServerHandler.sendChunkTiles(cx, cz, player.getHandle());
	}

}
