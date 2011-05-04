package org.bukkitcontrib.block;

import java.lang.reflect.Field;
import java.util.List;

import net.minecraft.server.Chunk;
import net.minecraft.server.ChunkProviderServer;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.util.ConcurrentSoftMap;

public class ContribCraftChunk extends CraftChunk{

    public ContribCraftChunk(Chunk chunk) {
        super(chunk);
    }
    
    @SuppressWarnings("unchecked")
    public ConcurrentSoftMap<Integer, Block> getCache() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field cache = CraftChunk.class.getDeclaredField("cache");
        cache.setAccessible(true);
        return (ConcurrentSoftMap<Integer, Block>) cache.get(this);
    }
    
    public Block getBlock(int x, int y, int z) {
        try {
            int pos = (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
            Block block = getCache().get( pos );
            if (block == null) {
                Block newBlock = new ContribCraftBlock( this, (getX() << 4) | (x & 0xF), y & 0x7F, (getZ() << 4) | (z & 0xF) );
                Block oldBlock = getCache().put( pos, newBlock );
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
                Field provider = CraftWorld.class.getDeclaredField("provider");
                provider.setAccessible(true);
                ChunkProviderServer cps = (ChunkProviderServer) provider.get(cw);
                for (Object c : cps.chunkList) {
                    Chunk chunk = (Chunk)c;
                    if (reset) {
                        if (chunk.bukkitChunk instanceof ContribCraftChunk) {
                            chunk.bukkitChunk = new CraftChunk(chunk);
                        }
                    }
                    else {
                        if (!(chunk.bukkitChunk instanceof ContribCraftChunk)) {
                            chunk.bukkitChunk = new ContribCraftChunk(chunk);
                        }
                    }
                }
            }
            catch (Exception e) {}
        }
    }
    
    public static boolean replaceBukkitChunk(org.bukkit.Chunk chunk) {
        if (!(((CraftChunk)chunk).getHandle().bukkitChunk instanceof ContribCraftChunk)) {
            ((CraftChunk)chunk).getHandle().bukkitChunk = new ContribCraftChunk(((CraftChunk)chunk).getHandle());
            return true;
        }
        return false;
    }
    
    public static void resetBukkitChunk(org.bukkit.Chunk chunk) {
        ((CraftChunk)chunk).getHandle().bukkitChunk = new CraftChunk(((CraftChunk)chunk).getHandle());
    }

}
