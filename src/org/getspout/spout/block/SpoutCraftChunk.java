package org.getspout.spout.block;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;
import net.minecraft.server.WorldServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.getspout.spoutapi.block.SpoutChunk;

public class SpoutCraftChunk extends CraftChunk implements SpoutChunk {
	protected final ConcurrentHashMap<Integer, Integer> queuedId = new ConcurrentHashMap<Integer, Integer>();
	protected final ConcurrentHashMap<Integer, Byte> queuedData = new ConcurrentHashMap<Integer, Byte>();
	protected static final Set<SpoutCraftChunk> queuedChunks = Collections.newSetFromMap(new ConcurrentHashMap<SpoutCraftChunk,Boolean>());
	public SpoutCraftChunk(Chunk chunk) {
		super(chunk);
	}
	
	@SuppressWarnings("unchecked")
	public Map<Integer, Block> getCache() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		Field cache = CraftChunk.class.getDeclaredField("cache");
		cache.setAccessible(true);
		return (Map<Integer, Block>) cache.get(this);
	}
	
	@Override
	public Block getBlock(int x, int y, int z) {
		try {
			int pos = (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
			Map<Integer, Block> cache = getCache();
			Block block = cache.get( pos );
			if (block == null) {
				Block newBlock = new SpoutCraftBlock( this, (getX() << 4) | (x & 0xF), y & 0x7F, (getZ() << 4) | (z & 0xF) );
				Block oldBlock = cache.put( pos, newBlock );
				if(oldBlock == null) {
					block = newBlock;
				} else {
					block = oldBlock;
				} 
			}
			return block;
		}
		catch (Exception e) {
			return super.getBlock(x, y, z);
		}
	}

	public boolean isLoaded() {
		return getWorld().isChunkLoaded(this);
	}

	public boolean load() {
		return getWorld().loadChunk(getX(), getZ(), true);
	}

	public boolean load(boolean generate) {
		return getWorld().loadChunk(getX(), getZ(), generate);
	}

	public boolean unload() {
		return getWorld().unloadChunk(getX(), getZ());
	}

	public boolean unload(boolean save) {
		return getWorld().unloadChunk(getX(), getZ(), save);
	}

	public boolean unload(boolean save, boolean safe) {
		return getWorld().unloadChunk(getX(), getZ(), save, safe);
	}
	
	public void onTick() {
		Iterator<Entry<Integer, Integer>> i = queuedId.entrySet().iterator();
		while(i.hasNext()) {
			Entry<Integer, Integer> entry = i.next();
			try {
				Block block = getCache().get(entry.getKey());
				block.setTypeId(entry.getValue());
				i.remove();
			}
			catch (Exception e) {
				
			}
		}
		Iterator<Entry<Integer, Byte>> j = queuedData.entrySet().iterator();
		while(i.hasNext()) {
			Entry<Integer, Byte> entry = j.next();
			try {
				Block block = getCache().get(entry.getKey());
				block.setData(entry.getValue());
				j.remove();
			}
			catch (Exception e) {
				
			}
		}
	}

	protected void onReset() {
		//TODO finalize queuing
	}
	
	public static void updateTicks() {
		Iterator<SpoutCraftChunk> i = SpoutCraftChunk.queuedChunks.iterator();
		while(i.hasNext()) {
			SpoutCraftChunk chunk = i.next();
			chunk.onTick();
			i.remove();
		}
	}

	public static void replaceAllBukkitChunks() {
		replaceAllBukkitChunks(false);
	}
	
	public static void resetAllBukkitChunks() {
		replaceAllBukkitChunks(true);
	}

	private static void replaceAllBukkitChunks(boolean reset) {
		List<World> worlds = Bukkit.getServer().getWorlds();
		for (World world : worlds) {
			try {
				CraftWorld cw = (CraftWorld)world;
				Field worldServer = CraftWorld.class.getDeclaredField("world");
				worldServer.setAccessible(true);
				ChunkProviderServer cps = ((WorldServer)worldServer.get(cw)).chunkProviderServer;
				for (Object c : cps.chunkList) {
					Chunk chunk = (Chunk)c;
					if (reset) {
						if (chunk.bukkitChunk instanceof SpoutCraftChunk) {
							((SpoutCraftChunk)chunk.bukkitChunk).onReset();
						}
						resetBukkitChunk(chunk.bukkitChunk);
					}
					else {
						replaceBukkitChunk(chunk.bukkitChunk);
					}
				}
			}
			catch (Exception e) {e.printStackTrace();}
		}
	}

	public static boolean replaceBukkitChunk(org.bukkit.Chunk chunk) {
		if (((CraftChunk)chunk).getHandle().bukkitChunk.getClass().hashCode() == SpoutCraftChunk.class.hashCode()) {
				return false; //hashcodes will differ if the class was constructed by a different version of this plugin
							  //or is a different class
		}
		((CraftChunk)chunk).getHandle().bukkitChunk = new SpoutCraftChunk(((CraftChunk)chunk).getHandle());
		return true;

	}
	
	public static void resetBukkitChunk(org.bukkit.Chunk chunk) {
		((CraftChunk)chunk).getHandle().bukkitChunk = new CraftChunk(((CraftChunk)chunk).getHandle());
	}

}
