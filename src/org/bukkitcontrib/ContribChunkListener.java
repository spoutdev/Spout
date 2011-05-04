package org.bukkitcontrib;

import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.WorldListener;
import org.bukkitcontrib.block.ContribCraftChunk;

public class ContribChunkListener extends WorldListener{
    
    @Override
    public void onChunkLoad(ChunkLoadEvent event) {
        ContribCraftChunk.replaceBukkitChunk(event.getChunk());
    }

}
