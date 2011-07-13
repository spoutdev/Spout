package org.bukkitcontrib;

import java.lang.reflect.Field;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkit.event.world.ChunkEvent;
import org.bukkitcontrib.block.ContribCraftChunk;

public class ContribChunkListener extends WorldListener{
    
    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        ContribCraftChunk.replaceBukkitChunk(event.getChunk());
        //update the reference to the chunk in the event
        try {
            Field chunk = ChunkEvent.class.getDeclaredField("chunk");
            chunk.setAccessible(true);
            chunk.set(event, event.getChunk().getWorld().getChunkAt(event.getChunk().getX(), event.getChunk().getZ()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
