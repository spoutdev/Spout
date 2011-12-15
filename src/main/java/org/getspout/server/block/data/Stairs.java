package org.getspout.server.block.data;

import org.bukkit.block.BlockFace;

public class Stairs extends Attachable {
    public Stairs(int id) {
        super(id, PlaceRequirement.ANYWHERE, AttachmentType.PLAYER_CARDINAL_DIRECTION);
    }

    @Override
    public BlockFace getAttachedFace(int existing) {
        switch (existing) {
        case 0x0:
        default:
            return BlockFace.NORTH;
        case 0x1:
            return BlockFace.SOUTH;
        case 0x2:
            return BlockFace.EAST;
        case 0x3:
            return BlockFace.WEST;
        }
    }

    @Override
    public int setAttachedFace(int existing, BlockFace target) {
        switch (target) {
        case NORTH:
        default:
            return 0x0;
        case SOUTH:
            return 0x1;
        case EAST:
            return 0x2;
        case WEST:
            return 0x3;
        }
    }
}
