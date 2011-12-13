package net.glowstone.block.physics;

import net.glowstone.block.BlockID;
import net.glowstone.block.GlowBlockState;
import net.glowstone.entity.GlowPlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

public class DoubleStepPhysics extends DefaultBlockPhysics {

    @Override
    public GlowBlockState placeAgainst(GlowPlayer player, GlowBlockState block, MaterialData data, BlockFace against) {
        if (against == BlockFace.UP && data.getItemTypeId() == BlockID.STEP) {
            GlowBlockState possibleStair = block.getBlock().getRelative(against.getOppositeFace()).getState();
            if (possibleStair.getTypeId() == BlockID.STEP && possibleStair.getRawData() == data.getData()) {
                possibleStair.setTypeId(BlockID.DOUBLE_STEP);
                possibleStair.setData(new MaterialData(BlockID.DOUBLE_STEP, data.getData()));
                return possibleStair;
            }
        }
        return super.placeAgainst(player, block, data, against);
    }
}