package org.getspout.server.block.data;

import org.bukkit.block.BlockFace;

public class Door extends ToggleableAttachable {
    public Door(int id) {
        super(id, PlaceRequirement.BLOCK_BELOW, AttachmentType.PLAYER_CARDINAL_DIRECTION);
    }

    @Override
    public boolean isOpen(int existing) {
        return (existing & 0x04) == 0x04;
    }

    @Override
    public int setOpen(int existing, boolean open) {
        return open ? existing | 0x04 : existing & ~0x04;
    }

    @Override
    public BlockFace getAttachedFace(int existing) {
        int data = existing & 0x3;
        switch (data) {
        case 0:
        default:
            return BlockFace.NORTH;

        case 1:
            return BlockFace.EAST;

        case 2:
            return BlockFace.SOUTH;

        case 3:
            return BlockFace.WEST;
        }
    }

    @Override
    public int setAttachedFace(int existing, BlockFace target) {
        int data = existing & 0x12;
        switch (target) {
        case EAST:
            data |= 0x1;
            break;

        case SOUTH:
            data |= 0x2;
            break;

        case WEST:
            data |= 0x3;
            break;
        }
        return data;
    }

    public int setTop(int existing, boolean top) {
        return top ? existing | 0x08 : existing & ~0x08;
    }

    public boolean isTop(int existing) {
        return (existing & 0x08) == 0x08;
    }
}
