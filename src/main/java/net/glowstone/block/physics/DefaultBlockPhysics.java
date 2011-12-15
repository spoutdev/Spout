package net.glowstone.block.physics;

import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlock;
import net.glowstone.block.GlowBlockState;
import net.glowstone.block.GlowWorkbench;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

public class DefaultBlockPhysics implements BlockPhysicsHandler {

    @Override
    public boolean canPlaceAt(GlowBlock loc, BlockFace against) {
        return true;
    }

    @Override
    public boolean doPhysics(GlowBlock block) {
        return false;
    }

    @Override
    public boolean postUpdateNeighbor(GlowBlock block, BlockFace against) {
        return false;
    }

    @Override
    public int getPlacedMetadata(GlowPlayer placer, int current, BlockFace against) {
        return current;
    }

    @Override
    public GlowBlockState placeAgainst(GlowPlayer player, GlowBlockState block, MaterialData data, BlockFace against) {
        block.setTypeId(data.getItemTypeId());
        data.setData((byte)getPlacedMetadata(player, data.getData(), against));
        block.setData(data);
        return block;
    }

    @Override
    public boolean interact(GlowPlayer player, GlowBlock block, boolean rightClick, BlockFace against) {
        if(block.isInteractable())
        {
            int blockType = block.getTypeId();
            
            if(blockType == BlockID.WORKBENCH)
            {
                GlowWorkbench.interacted(player, rightClick, against);
                return false;
            }
            else return block.interacted(player, rightClick, against);
        }
        else
        {
            return true;
        }
    }
}
