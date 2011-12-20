package org.getspout.server.block.data;

import org.bukkit.block.BlockFace;

import org.getspout.server.block.BlockID;

public class Trapdoor extends ToggleableAttachable {
	public Trapdoor() {
		super(BlockID.TRAP_DOOR, PlaceRequirement.ATTACHED_BLOCK_SIDE, AttachmentType.CLICKED_BLOCK);
	}

	@Override
	public int toggleOpen(int existing) {
		return existing ^ 4;
	}

	@Override
	public int setOpen(int existing, boolean open) {
		return open ? existing | 0x04 : existing & ~0x04;
	}

	@Override
	public boolean isOpen(int existing) {
		return (existing & 0x04) == 0x4;
	}

	@Override
	public BlockFace getAttachedFace(int existing) {
		byte data = (byte) (existing & 0x3);
		switch (data) {
			case 0x1:
				return BlockFace.EAST;
			case 0x2:
				return BlockFace.NORTH;
			case 0x3:
				return BlockFace.SOUTH;
			default:
			case 0x0:
				return BlockFace.WEST;
		}
	}

	@Override
	public int setAttachedFace(int existing, BlockFace target) {
		byte data = (byte) (existing & 0x4);
		switch (target) {
			case WEST:
				data |= 0x1;
				break;
			case NORTH:
				data |= 0x2;
				break;
			case SOUTH:
				data |= 0x3;
				break;
		}

		return data;
	}
}
