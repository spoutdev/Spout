package net.glowstone.block.physics;

import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

public class DefaultBlockPhysics implements BlockPhysicsHandler {

    public boolean canPlaceAt(GlowBlock loc, BlockFace against) {
        return true;
    }

    public boolean doPhysics(GlowBlock block) {
        return false;
    }

    public boolean postUpdateNeighbor(GlowBlock block, BlockFace against) {
        return false;
    }

    public int getPlacedMetadata(GlowPlayer placer, int current, BlockFace against) {
        return current;
    }

    public GlowBlockState placeAgainst(GlowPlayer player, GlowBlockState block, MaterialData data, BlockFace against) {
        block.setTypeId(data.getItemTypeId());
        data.setData((byte)getPlacedMetadata(player, data.getData(), against));
        block.setData(data);
        return block;
    }

    public boolean interact(GlowPlayer player, GlowBlock block, boolean rightClick, BlockFace against) {
        return true;
    }
}
