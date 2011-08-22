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
	
	public static long intPairToLong(int x, int z) {
		return (((long)x)<<32) | (((long)z) & 0xFFFFFFFFL);
	}

}
