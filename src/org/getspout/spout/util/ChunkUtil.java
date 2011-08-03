package org.getspout.spout.util;

import java.util.ArrayList;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.CraftWorld;
import org.getspout.spout.block.SpoutCraftChunk;
import org.getspout.spoutapi.block.SpoutChunk;

public abstract class ChunkUtil {
	
	public static ArrayList<SpoutChunk> getLoadedChunks(World world) {
		ArrayList<SpoutChunk> chunkList = null;
		try {
			CraftWorld cw = (CraftWorld)world;
			ChunkProviderServer cps = cw.getHandle().chunkProviderServer;
			chunkList = new ArrayList<SpoutChunk>(cps.chunkList.size());
			for (Object c : cps.chunkList) {
				Chunk chunk = (Chunk)c;
				if (!(chunk.bukkitChunk instanceof SpoutCraftChunk)) {
					chunk.bukkitChunk = new SpoutCraftChunk(chunk);
				}
				chunkList.add((SpoutChunk)chunk.bukkitChunk);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
			return new ArrayList<SpoutChunk>();
		}
		return chunkList;
	}
	
	public static ArrayList<SpoutChunk> getAllLoadedChunks() {
		ArrayList<SpoutChunk> chunkList = new ArrayList<SpoutChunk>();
		for (World w : Bukkit.getServer().getWorlds()) {
			chunkList.addAll(getLoadedChunks(w));
		}
		return chunkList;
	}

}
