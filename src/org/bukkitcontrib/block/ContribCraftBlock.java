package org.bukkitcontrib.block;

import org.bukkit.craftbukkit.CraftChunk;
import org.bukkit.craftbukkit.block.CraftBlock;

public class ContribCraftBlock extends CraftBlock{
    protected final int x, y, z;
    protected final ContribCraftChunk chunk;
    public ContribCraftBlock(ContribCraftChunk chunk, int x, int y, int z) {
        super(chunk, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk = chunk
    }

    @Override
    public ContribCraftChunk getChunk() {
        return chunk;
    }
    
}
