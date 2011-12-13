package net.glowstone.item.physics;

import net.glowstone.block.BlockID;
import net.glowstone.item.ItemID;
import org.bukkit.block.BlockFace;
import org.bukkit.material.MaterialData;

public class SignItemPhysics extends DefaultItemPhysics {

    public SignItemPhysics() {
        super(ItemID.SIGN);
    }

    public MaterialData getPlacedBlock(BlockFace against, int data) {
        if (against == BlockFace.UP)  {
            return new MaterialData(BlockID.SIGN_POST);
        } else {
            return new MaterialData(BlockID.WALL_SIGN);
        }
    }
}
