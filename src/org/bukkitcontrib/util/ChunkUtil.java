package org.bukkitcontrib.util;

import java.util.ArrayList;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkitcontrib.block.ContribChunk;
import org.bukkitcontrib.block.ContribCraftChunk;

public abstract class ChunkUtil {
	
	public static ArrayList<ContribChunk> getLoadedChunks(World world) {
		ArrayList<ContribChunk> chunkList = null;
		try {
			CraftWorld cw = (CraftWorld)world;
			ChunkProviderServer cps = cw.getHandle().chunkProviderServer;
			chunkList = new ArrayList<ContribChunk>(cps.chunkList.size());
			for (Object c : cps.chunkList) {
				Chunk chunk = (Chunk)c;
				if (!(chunk.bukkitChunk instanceof ContribCraftChunk)) {
					chunk.bukkitChunk = new ContribCraftChunk(chunk);
				}
				chunkList.add((ContribChunk)chunk.bukkitChunk);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<ContribChunk>();
		}
		return chunkList;
	}
	
	public static ArrayList<ContribChunk> getAllLoadedChunks() {
		ArrayList<ContribChunk> chunkList = new ArrayList<ContribChunk>();
		for (World w : Bukkit.getServer().getWorlds()) {
			chunkList.addAll(getLoadedChunks(w));
		}
		return chunkList;
	}

}
