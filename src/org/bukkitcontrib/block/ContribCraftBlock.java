package org.bukkitcontrib.block;

import org.bukkit.Material;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;

public class ContribCraftBlock extends CraftBlock implements ContribBlock {
    protected final int x, y, z;
    protected final ContribCraftChunk chunk;
    public ContribCraftBlock(ContribCraftChunk chunk, int x, int y, int z) {
        super(chunk, x, y, z);
        this.x = x;
        this.y = y;
        this.z = z;
        this.chunk = chunk;
    }

    @Override
    public ContribCraftChunk getChunk() {
        return chunk;
    }
    
    public void setTypeAsync(Material type) {
        setTypeIdAsync(type.getId());
    }
    
    public void setTypeIdAsync(int type) {
        chunk.queuedId.put(getIndex(), type);
        final byte data = getData();
        for (Player player : chunk.getWorld().getPlayers()) {
            player.sendBlockChange(getLocation(), type, data);
        }
    }
    
    public void setDataAsync(byte data) {
        chunk.queuedData.put(getIndex(), data);
        final int type = getTypeId();
        for (Player player : chunk.getWorld().getPlayers()) {
            player.sendBlockChange(getLocation(), type, data);
        }
    }
    
    public void setTypeIdAndDataAsync(int type, byte data) {
        chunk.queuedId.put(getIndex(), type);
        chunk.queuedData.put(getIndex(), data);
        for (Player player : chunk.getWorld().getPlayers()) {
            player.sendBlockChange(getLocation(), type, data);
        }
    }
    
    private int getIndex() {
        return (x & 0xF) << 11 | (z & 0xF) << 7 | (y & 0x7F);
    }
    
}
