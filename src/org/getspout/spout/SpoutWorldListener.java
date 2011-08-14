package org.getspout.spout;

import java.lang.reflect.Field;

import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.event.world.ChunkEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.WorldLoadEvent;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spout.chunkstore.SimpleChunkDataManager;
import org.getspout.spoutapi.SpoutManager;

public class SpoutWorldListener extends WorldListener{
	
	@Override
	public void onChunkLoad(ChunkLoadEvent event) {
		SpoutCraftChunk.replaceBukkitChunk(event.getChunk());
		//update the reference to the chunk in the event
		try {
			Field chunk = ChunkEvent.class.getDeclaredField("chunk");
			chunk.setAccessible(true);
			chunk.set(event, event.getChunk().getWorld().getChunkAt(event.getChunk().getX(), event.getChunk().getZ()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getInstance().getChunkDataManager();
		dm.loadChunk(event.getChunk());
	}

	@Override
	public void onWorldLoad(WorldLoadEvent event) {
		net.minecraft.server.World world = ((CraftWorld)event.getWorld()).getHandle();
		SpoutPlayerManagerTransfer.replacePlayerManager((net.minecraft.server.WorldServer)world);
		
		SimpleChunkDataManager dm = (SimpleChunkDataManager)SpoutManager.getInstance().getChunkDataManager();
		dm.loadWorldChunks(event.getWorld());
	}
}
